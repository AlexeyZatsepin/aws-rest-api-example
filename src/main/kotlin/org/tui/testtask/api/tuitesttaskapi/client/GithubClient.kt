package org.tui.testtask.api.tuitesttaskapi.client

import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import org.tui.testtask.api.tuitesttaskapi.error.GithubResourceNotFoundException
import org.tui.testtask.api.tuitesttaskapi.error.ServiceNotAvailableException
import org.tui.testtask.api.tuitesttaskapi.model.dto.BranchesResponse
import org.tui.testtask.api.tuitesttaskapi.model.dto.RepositoryResponse
import reactor.core.publisher.Mono

private const val DEFAULT_PAGE_SIZE = 30
private const val DEFAULT_PAGE_NUM = 1

class GithubClient(private val webClient: WebClient) {

    fun getAllRepositories(
        username: String, page: Int = DEFAULT_PAGE_NUM,
        perPage: Int = DEFAULT_PAGE_SIZE,
        direction: String = "asc"
    ) = webClient.get()
        .uri { builder ->
            builder
                .path("/users/{username}/repos")
                .queryParam("page", page)
                .queryParam("per_page", perPage)
                .queryParam("direction", direction)
                .build(username)
        }
        .retrieve()
        .onStatus(
            { httpStatus -> HttpStatus.NOT_FOUND == httpStatus },
            { Mono.error(GithubResourceNotFoundException(message = "Repository for $username not found.")) })
        .onStatus(
            { httpStatus -> httpStatus.is5xxServerError },
            { Mono.error(ServiceNotAvailableException()) })
        .bodyToFlux(RepositoryResponse::class.java)


    fun getAllBranches(
        username: String,
        repo: String,
        page: Int = DEFAULT_PAGE_NUM,
        perPage: Int = DEFAULT_PAGE_SIZE
    ) = webClient.get()
        .uri { builder ->
            builder
                .path("/repos/{username}/{repo}/branches")
                .queryParam("page", page)
                .queryParam("per_page", perPage)
                .build(username, repo)
        }
        .retrieve()
        .onStatus(
            { httpStatus -> HttpStatus.NOT_FOUND == httpStatus },
            { Mono.error(GithubResourceNotFoundException(message = "Branches for $username/$repo not found.")) })
        .onStatus(
            { httpStatus -> httpStatus.is5xxServerError },
            { Mono.error(ServiceNotAvailableException()) })
        .bodyToFlux(BranchesResponse::class.java)
}