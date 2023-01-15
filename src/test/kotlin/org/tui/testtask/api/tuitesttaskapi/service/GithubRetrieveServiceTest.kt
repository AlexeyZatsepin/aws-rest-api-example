package org.tui.testtask.api.tuitesttaskapi.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.tui.testtask.api.tuitesttaskapi.client.GithubClient
import org.tui.testtask.api.tuitesttaskapi.mapping.RepositoryMapper
import org.tui.testtask.api.tuitesttaskapi.model.Repository
import org.tui.testtask.api.tuitesttaskapi.model.dto.BranchesResponse
import org.tui.testtask.api.tuitesttaskapi.model.dto.Commit
import org.tui.testtask.api.tuitesttaskapi.model.dto.Owner
import org.tui.testtask.api.tuitesttaskapi.model.dto.RepositoryResponse
import reactor.test.StepVerifier
import reactor.test.publisher.TestPublisher


private const val ORGANIZATION = "test"

class GithubRetrieveServiceTest {

    private lateinit var githubClient: GithubClient
    private lateinit var mapper: RepositoryMapper
    private lateinit var service: GithubRetrieveService


    @BeforeEach
    fun init() {
        githubClient = mock(GithubClient::class.java)
        mapper = mock(RepositoryMapper::class.java)

        service = GithubRetrieveService(githubClient, mapper)

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
        val repositoryResponse2 = RepositoryResponse(
            name = "test-repo2",
            owner = Owner(
                login = "username"
            ),
            fork = true
        )
        val repositoryResponse3 = RepositoryResponse(
            name = "test-repo3",
            owner = Owner(
                login = "username"
            ),
            fork = false
        )

        `when`(githubClient.getAllRepositories(ORGANIZATION))
            .thenReturn(TestPublisher.createCold<RepositoryResponse>()
                .emit(repositoryResponse1)
                .emit(repositoryResponse2)
                .emit(repositoryResponse3)
                .flux())

        `when`(githubClient.getAllBranches(ORGANIZATION, "test-repo1"))
            .thenReturn(TestPublisher.createCold<BranchesResponse>()
                .emit(branchesResponse1)
                .emit(branchesResponse2)
                .flux())
        `when`(githubClient.getAllBranches(ORGANIZATION, "test-repo3"))
            .thenReturn(TestPublisher.createCold<BranchesResponse>()
                .emit(branchesResponse1)
                .emit(branchesResponse2)
                .flux())

        `when`(mapper.map(repositoryResponse1, listOf(branchesResponse1, branchesResponse2)))
            .thenReturn(Repository(name = "test-repo1"))

        `when`(mapper.map(repositoryResponse3, listOf(branchesResponse1, branchesResponse2)))
            .thenReturn(Repository(name = "test-repo3"))
    }

    @Test
    fun `should retrieve github repositories for organization`() {
        StepVerifier
            .create(service.retrieveRepositories(ORGANIZATION, 1, 30))
            .expectSubscription()
            .assertNext { println(it) }
            .assertNext { println(it) }
            .expectComplete()
            .verify()
    }
}