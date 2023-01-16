package org.tui.testtask.api.tuitesttaskapi.client

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import org.tui.testtask.api.tuitesttaskapi.error.ServiceNotAvailableException
import org.tui.testtask.api.tuitesttaskapi.model.dto.BranchesResponse
import org.tui.testtask.api.tuitesttaskapi.model.dto.RepositoryResponse
import reactor.test.StepVerifier
import java.io.IOException


class GithubClientTest {

    var githubClient: GithubClient = GithubClient(WebClient.create(mockBackEnd.url("/").toString()))

    @Test
    @Throws(Exception::class)
    fun shouldGetAllRepositories() {
        mockBackEnd.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonMapper.writeValueAsString(RepositoryResponse(name="test", fork = true)))
                .addHeader("Content-Type", "application/json")
        )
        StepVerifier.create(githubClient.getAllRepositories("test"))
            .assertNext {
                assertEquals("test", it.name)
                assertTrue(it.fork)
            }
            .verifyComplete()
    }

    @Test
    @Throws(Exception::class)
    fun shouldMapNotFoundExceptionForRepositories() {
        mockBackEnd.enqueue(
            MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json")
        )
        StepVerifier.create(githubClient.getAllRepositories("test"))
            .expectErrorMatches{
                it.message == "Repository for test not found."
            }
            .verify()
    }

    @Test
    @Throws(Exception::class)
    fun shouldMapServerExceptionForRepositories() {
        mockBackEnd.enqueue(
            MockResponse()
                .setResponseCode(500)
        )
        StepVerifier.create(githubClient.getAllRepositories("test"))
            .expectErrorMatches{
                it is ServiceNotAvailableException
            }
            .verify()
    }

    @Test
    @Throws(Exception::class)
    fun shouldGetAllBranches() {
        mockBackEnd.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonMapper.writeValueAsString(BranchesResponse(name="test")))
                .addHeader("Content-Type", "application/json")
        )
        StepVerifier.create(githubClient.getAllBranches("test","test"))
            .assertNext {
                assertEquals("test", it.name)
            }
            .verifyComplete()
    }

    @Test
    @Throws(Exception::class)
    fun shouldMapNotFoundExceptionForBranches() {
        mockBackEnd.enqueue(
            MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json")
        )
        StepVerifier.create(githubClient.getAllBranches("test","test"))
            .expectErrorMatches{
                it.message == "Branches for test/test not found."
            }
            .verify()
    }

    @Test
    @Throws(Exception::class)
    fun shouldMapServerExceptionForBranches() {
        mockBackEnd.enqueue(
            MockResponse()
                .setResponseCode(500)
        )
        StepVerifier.create(githubClient.getAllBranches("test", "test"))
            .expectErrorMatches{
                it is ServiceNotAvailableException
            }
            .verify()
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