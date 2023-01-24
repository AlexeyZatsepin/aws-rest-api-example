package org.tui.testtask.api.tuitesttaskapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.tui.testtask.api.tuitesttaskapi.client.GithubClient
import org.tui.testtask.api.tuitesttaskapi.client.dto.BranchesResponse
import org.tui.testtask.api.tuitesttaskapi.client.dto.Commit
import org.tui.testtask.api.tuitesttaskapi.client.dto.Owner
import org.tui.testtask.api.tuitesttaskapi.client.dto.RepositoryResponse
import org.tui.testtask.api.tuitesttaskapi.error.GithubResourceNotFoundException
import org.tui.testtask.api.tuitesttaskapi.error.ServiceNotAvailableException
import org.tui.testtask.api.tuitesttaskapi.mapping.RepositoryMapper
import org.tui.testtask.api.tuitesttaskapi.model.Branch
import org.tui.testtask.api.tuitesttaskapi.model.Repository
import reactor.test.StepVerifier
import reactor.test.publisher.TestPublisher


private const val USERNAME = "test"

@ExtendWith(MockitoExtension::class)
class GithubRepositoryServiceTest {
    @Mock
    private lateinit var githubClient: GithubClient
    @Mock
    private lateinit var mapper: RepositoryMapper
    @Mock
    private lateinit var dataGenerator: PageDataGeneratorService<Pair<String,String>, BranchesResponse>

    @InjectMocks
    private lateinit var service: GithubRepositoryService

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
        whenever(githubClient.getAllRepositories(USERNAME))
            .thenReturn(TestPublisher.createCold<RepositoryResponse>()
                .emit(repositoryResponse1)
                .flux())

        whenever(dataGenerator.generateDataFlux(any(), any()))
            .thenReturn(TestPublisher.createCold<BranchesResponse>()
                .emit(branchesResponse1)
                .emit(branchesResponse2)
                .flux())

        whenever(mapper.map(repositoryResponse1, listOf(branchesResponse1, branchesResponse2)))
            .thenReturn(Repository(name = "test-repo1", owner = "username", branches = listOf(Branch(), Branch())))

        StepVerifier
            .create(service.retrieveRepositories(USERNAME, false, 1, 30))
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
        whenever(githubClient.getAllRepositories(USERNAME))
            .thenReturn(TestPublisher.createCold<RepositoryResponse>()
                .emit(repositoryResponse1)
                .flux())

        StepVerifier
            .create(service.retrieveRepositories(USERNAME, false,1, 30))
            .expectSubscription()
            .expectComplete()
            .verify()
    }

    @Test
    fun `should not handle not found exception from API`() {
        whenever(githubClient.getAllRepositories(USERNAME))
            .thenReturn(TestPublisher.createCold<RepositoryResponse>()
                .error(GithubResourceNotFoundException("")).flux())

        StepVerifier
            .create(service.retrieveRepositories(USERNAME, false, 1, 30))
            .expectError()
            .verify()
    }

    @Test
    fun `should not handle server exception from API`() {
        whenever(githubClient.getAllRepositories(USERNAME))
            .thenReturn(TestPublisher.createCold<RepositoryResponse>()
                .error(ServiceNotAvailableException()).flux())

        StepVerifier
            .create(service.retrieveRepositories(USERNAME, false, 1, 30))
            .expectError()
            .verify()
    }

    private fun <T> any() : T {
        return org.mockito.ArgumentMatchers.any()
    }
}