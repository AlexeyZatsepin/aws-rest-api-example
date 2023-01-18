package org.tui.testtask.api.tuitesttaskapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.tui.testtask.api.tuitesttaskapi.client.GithubClient
import org.tui.testtask.api.tuitesttaskapi.error.GithubResourceNotFoundException
import org.tui.testtask.api.tuitesttaskapi.error.ServiceNotAvailableException
import org.tui.testtask.api.tuitesttaskapi.mapping.RepositoryMapper
import org.tui.testtask.api.tuitesttaskapi.model.Branch
import org.tui.testtask.api.tuitesttaskapi.model.Repository
import org.tui.testtask.api.tuitesttaskapi.model.dto.BranchesResponse
import org.tui.testtask.api.tuitesttaskapi.model.dto.Commit
import org.tui.testtask.api.tuitesttaskapi.model.dto.Owner
import org.tui.testtask.api.tuitesttaskapi.model.dto.RepositoryResponse
import reactor.test.StepVerifier
import reactor.test.publisher.TestPublisher


private const val ORGANIZATION = "test"

@ExtendWith(MockitoExtension::class)
class GithubRetrieveServiceTest {
    @Mock
    private lateinit var githubClient: GithubClient
    @Mock
    private lateinit var mapper: RepositoryMapper
    @Mock
    private lateinit var dataGenerator: PageDataGeneratorService<Pair<String,String>,BranchesResponse>

    private lateinit var service: GithubRetrieveService


    @BeforeEach
    fun init() {
        service = GithubRetrieveService(githubClient, mapper, dataGenerator)
    }

    @Test
    fun `should retrieve github repositories for organization`() {
        val branchesResponse1 = BranchesResponse(
            name="main",
            commit = Commit(
                sha = "12345"
            )
        )
        val branchesResponse2 = BranchesResponse(
            name="develop",
            commit = Commit(
                sha = "12345"
            )
        )
        val repositoryResponse1 = RepositoryResponse(
            name = "test-repo1",
            owner = Owner(
                login = "username"
            ),
            fork = false
        )
        `when`(githubClient.getAllRepositories(ORGANIZATION))
            .thenReturn(TestPublisher.createCold<RepositoryResponse>()
                .emit(repositoryResponse1)
                .flux())

        `when`(dataGenerator.generateDataFlux(any(), any()))
            .thenReturn(TestPublisher.createCold<BranchesResponse>()
                .emit(branchesResponse1)
                .emit(branchesResponse2)
                .flux())

        `when`(mapper.map(repositoryResponse1, listOf(branchesResponse1, branchesResponse2)))
            .thenReturn(Repository(name = "test-repo1", owner = "username", branches = listOf(Branch(), Branch())))

        StepVerifier
            .create(service.retrieveRepositories(ORGANIZATION, 1, 30))
            .expectSubscription()
            .assertNext {
                assertThat(it.name).isEqualTo("test-repo1")
                assertThat(it.owner).isEqualTo("username")
                assertThat(it.branches).hasSize(2)
            }
            .expectComplete()
            .verify()
    }

    @Test
    fun `should filter out forked repositories`() {
        val repositoryResponse1 = RepositoryResponse(
            name = "test-repo1",
            owner = Owner(
                login = "username"
            ),
            fork = true
        )
        `when`(githubClient.getAllRepositories(ORGANIZATION))
            .thenReturn(TestPublisher.createCold<RepositoryResponse>()
                .emit(repositoryResponse1)
                .flux())

        StepVerifier
            .create(service.retrieveRepositories(ORGANIZATION, 1, 30))
            .expectSubscription()
            .expectComplete()
            .verify()
    }

    @Test
    fun `should not handle not found exception from API`() {
        `when`(githubClient.getAllRepositories(ORGANIZATION))
            .thenReturn(TestPublisher.createCold<RepositoryResponse>()
                .error(GithubResourceNotFoundException("")).flux())

        StepVerifier
            .create(service.retrieveRepositories(ORGANIZATION, 1, 30))
            .expectError()
            .verify()
    }

    @Test
    fun `should not handle server exception from API`() {
        `when`(githubClient.getAllRepositories(ORGANIZATION))
            .thenReturn(TestPublisher.createCold<RepositoryResponse>()
                .error(ServiceNotAvailableException()).flux())

        StepVerifier
            .create(service.retrieveRepositories(ORGANIZATION, 1, 30))
            .expectError()
            .verify()
    }

    private fun <T> any() : T {
        return org.mockito.ArgumentMatchers.any()
    }
}