package org.tui.testtask.api.tuitesttaskapi.client

import org.springframework.web.reactive.function.client.WebClient
import org.tui.testtask.api.tuitesttaskapi.model.dto.BranchesResponse
import org.tui.testtask.api.tuitesttaskapi.model.dto.RepositoryResponse

class GithubClient(private val webClient: WebClient) {

    fun getAllRepositories(org: String) = webClient.get()
            .uri("/orgs/{org}/repos", org)
            .retrieve()
            .bodyToFlux(RepositoryResponse::class.java)

    fun getAllBranches(org: String, repo: String) = webClient.get()
        .uri("/repos/{org}/{repo}/branches", org, repo)
        .retrieve()
        .bodyToFlux(BranchesResponse::class.java)
}