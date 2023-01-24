package org.tui.testtask.api.tuitesttaskapi.it

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.tui.testtask.api.tuitesttaskapi.model.Repository

private const val TEST_PATH = "/v1/user/octocat/repos"

private const val GITHUB_REPO_TEST_PATH = "/users/octocat/repos"
private const val GITHUB_BRANCH_TEST_PATH = "/repos/octocat/Hello-World/branches"

@WireMockTest(httpsEnabled = false, httpPort = 80)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubApiIntegrationTest {

    @Autowired
    lateinit var client: WebTestClient

    @Test
    fun whenRequestRepository_thenStatusShouldBeOk() {
        stubFor(get(urlPathEqualTo(GITHUB_REPO_TEST_PATH))
            .withHeader("Authorization", containing("sometoken"))
            .withQueryParam("page", equalTo("1"))
            .withQueryParam("per_page", equalTo("30"))
            .withQueryParam("direction", equalTo("asc"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile("github/repository-response.json")))

        stubFor(get(urlPathEqualTo(GITHUB_BRANCH_TEST_PATH))
            .withHeader("Authorization", containing("sometoken"))
            .withQueryParam("page", equalTo("1"))
            .withQueryParam("per_page", equalTo("30"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile("github/branches-response-page1.json")))

        stubFor(get(urlPathEqualTo(GITHUB_BRANCH_TEST_PATH))
            .withHeader("Authorization", containing("sometoken"))
            .withQueryParam("page", equalTo("2"))
            .withQueryParam("per_page", equalTo("30"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBodyFile("github/empty-response.json")))

        client.get()
            .uri(TEST_PATH)
            .header(HttpHeaders.AUTHORIZATION, "Bearer test")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Repository::class.java)
    }


    @Test
    fun whenRequestWrongRepository_thenStatusShouldBe404() {
        stubFor(get(urlPathEqualTo(GITHUB_REPO_TEST_PATH))
            .withHeader("Authorization", containing("sometoken"))
            .withQueryParam("page", equalTo("1"))
            .withQueryParam("per_page", equalTo("30"))
            .withQueryParam("direction", equalTo("asc"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.NOT_FOUND.value())))

        client.get()
            .uri(TEST_PATH)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo("404")
            .jsonPath("$.message").isEqualTo("Repository for octocat not found")
    }

}