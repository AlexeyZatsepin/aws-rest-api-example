package org.tui.testtask.api.tuitesttaskapi.controller

import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.tui.testtask.api.tuitesttaskapi.error.GithubResourceNotFoundException
import org.tui.testtask.api.tuitesttaskapi.model.Branch
import org.tui.testtask.api.tuitesttaskapi.model.Repository
import org.tui.testtask.api.tuitesttaskapi.security.WebSecurityConfig
import org.tui.testtask.api.tuitesttaskapi.service.GithubRepositoryService
import reactor.test.publisher.TestPublisher

private const val TEST_URL = "/v1/user/test/repos"

@WebFluxTest(controllers = [VcsRepoController::class])
@Import(WebSecurityConfig::class)
class VcsRepoControllerTest {

    @Autowired
    lateinit var client: WebTestClient

    @MockBean
    lateinit var service: GithubRepositoryService

    @Test
    fun whenRequestRepository_thenStatusShouldBeOk() {
        whenever(service.retrieveRepositories("test", false, 1, 30))
            .thenReturn(
                TestPublisher.createCold<Repository>()
                    .emit(
                        Repository(
                            name = "test-repo",
                            owner = "John",
                            branches = listOf(Branch("main", "12345"))
                        )
                    )
                    .flux()
            )
        client.get()
            .uri(TEST_URL)
            .header(HttpHeaders.AUTHORIZATION, "Bearer test")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Repository::class.java)
    }

    @Test
    fun whenRequestRepositoryWithNegativeSize_thenStatusShouldBe400() {
        client.get()
            .uri("$TEST_URL?size=-100")
            .header(HttpHeaders.AUTHORIZATION, "Bearer test")
            .exchange()
            .expectStatus().is4xxClientError
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo("400")
            .jsonPath("$.message").isEqualTo("getAllRepositories.size: must be greater than 0")
    }

    @Test
    fun whenRequestRepositoryWithWrongSize_thenStatusShouldBe400() {
        client.get()
            .uri("$TEST_URL?size=500")
            .header(HttpHeaders.AUTHORIZATION, "Bearer test")
            .exchange()
            .expectStatus().is4xxClientError
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo("400")
            .jsonPath("$.message").isEqualTo("getAllRepositories.size: must be less than or equal to 100")
    }

    @Test
    fun whenRequestRepositoryWithNegativePage_thenStatusShouldBe400() {
        client.get()
            .uri("$TEST_URL?page=-100")
            .header(HttpHeaders.AUTHORIZATION, "Bearer test")
            .exchange()
            .expectStatus().is4xxClientError
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo("400")
            .jsonPath("$.message").isEqualTo("getAllRepositories.page: must be greater than 0")
    }

    @Test
    fun whenRequestWrongRepository_thenStatusShouldBe404() {
        whenever(service.retrieveRepositories("test", false, 1, 30))
            .thenReturn(
                TestPublisher.createCold<Repository>()
                    .error(GithubResourceNotFoundException("Repository test not found"))
                    .flux()
            )

        client.get()
            .uri(TEST_URL)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo("404")
            .jsonPath("$.message").isEqualTo("Repository test not found")
    }

    @Test
    fun whenHeaderIsWrong_thenStatusShouldBe406() {
        client.get()
            .uri(TEST_URL)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
            .exchange()
            .expectStatus().is4xxClientError
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo("406")
            .jsonPath("$.message").isEqualTo("Could not find acceptable representation")
    }

}
