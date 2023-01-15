package org.tui.testtask.api.tuitesttaskapi.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.tui.testtask.api.tuitesttaskapi.model.Branch
import org.tui.testtask.api.tuitesttaskapi.model.Repository
import org.tui.testtask.api.tuitesttaskapi.service.GithubRetrieveService
import reactor.test.publisher.TestPublisher

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VcsRepoControllerTest {

    lateinit var client: WebTestClient

    lateinit var service: GithubRetrieveService

    lateinit var controller: VcsRepoController

    @BeforeEach
    fun setup() {
        service = Mockito.mock(GithubRetrieveService::class.java)

        controller = VcsRepoController(service)

        client = WebTestClient.bindToController(controller).build()

        Mockito.`when`(service.retrieveRepositories("gyroflow"))
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
    }

    @Test
    fun whenRequestProfile_thenStatusShouldBeOk() {
        client.get()
            .uri("/v1/repos/gyroflow")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Repository::class.java)

    }


}