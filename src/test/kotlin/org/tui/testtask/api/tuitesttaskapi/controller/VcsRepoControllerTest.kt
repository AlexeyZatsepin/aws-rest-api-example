package org.tui.testtask.api.tuitesttaskapi.controller

import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.test.web.reactive.server.WebTestClient
import org.tui.testtask.api.tuitesttaskapi.error.GithubResourceNotFoundException
import org.tui.testtask.api.tuitesttaskapi.model.Branch
import org.tui.testtask.api.tuitesttaskapi.model.Repository
import org.tui.testtask.api.tuitesttaskapi.service.GithubRetrieveService
import reactor.test.publisher.TestPublisher

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VcsRepoControllerTest {

    @Autowired
    lateinit var client: WebTestClient

    @MockBean
    lateinit var service: GithubRetrieveService

    @Test
    fun whenRequestRepository_thenStatusShouldBeOk() {
        Mockito.`when`(service.retrieveRepositories("gyroflow", 1, 30))
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
            .uri("/v1/repos/gyroflow")
            .header(HttpHeaders.AUTHORIZATION, "Bearer test")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Repository::class.java)
    }

    @Test
    fun whenRequestWrongRepository_thenStatusShouldBe404() {
        Mockito.`when`(service.retrieveRepositories("gyroflow", 1, 30))
            .thenReturn(
                TestPublisher.createCold<Repository>()
                    .error(GithubResourceNotFoundException("Repository gyroflow not found"))
                    .flux()
            )

        client.get()
            .uri("/v1/repos/gyroflow")
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo("404")
            .jsonPath("$.message").isEqualTo("Repository gyroflow not found")
    }

    @Test
    fun whenHeaderIsWrong_thenStatusShouldBe406() {
        client.get()
            .uri("/v1/repos/gyroflow")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
            .exchange()
            .expectStatus().is4xxClientError
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo("406")
            .jsonPath("$.message").isEqualTo("Could not find acceptable representation")
    }

    @TestConfiguration
    class TestSecurityConfiguration {
        @Bean
        fun springSecurityFilterChain(
            http: ServerHttpSecurity
        ): SecurityWebFilterChain {
            http.authorizeExchange()
                .pathMatchers("/**").permitAll()
            return http.build()
        }
    }

}