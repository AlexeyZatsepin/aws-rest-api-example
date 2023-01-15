package org.tui.testtask.api.tuitesttaskapi.client

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import org.tui.testtask.api.tuitesttaskapi.model.dto.RepositoryResponse
import reactor.test.StepVerifier
import java.io.IOException


class GithubClientTest {

    var githubClient: GithubClient = GithubClient(WebClient.create(mockBackEnd.url("/").toString()))

    @Test
    @Throws(Exception::class)
    fun getAllRepositories() {
        mockBackEnd.enqueue(
            MockResponse()
                .setBody(jsonMapper.writeValueAsString(RepositoryResponse(name="test", fork = true)))
                .addHeader("Content-Type", "application/json")
        )
        StepVerifier.create(githubClient.getAllRepositories("test"))
            .assertNext { it.fork && it.name == "test" }
            .verifyComplete()
    }

    companion object {
        lateinit var mockBackEnd: MockWebServer
        lateinit var jsonMapper: ObjectMapper

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun setUp() {
            mockBackEnd = MockWebServer()
            jsonMapper = ObjectMapper()
            mockBackEnd.start()
        }

        @JvmStatic
        @AfterAll
        @Throws(IOException::class)
        fun tearDown() {
            mockBackEnd.shutdown()
        }
    }
}