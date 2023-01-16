package org.tui.testtask.api.tuitesttaskapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.tui.testtask.api.tuitesttaskapi.client.GithubClient
import org.tui.testtask.api.tuitesttaskapi.mapping.RepositoryMapper
import org.tui.testtask.api.tuitesttaskapi.service.GithubRetrieveService
import java.util.*


@Configuration
@ConditionalOnProperty(name = ["vcs.type"], havingValue = "github")
class GithubApiAppConfiguration {
    @Bean
    fun githubWebClient(@Value("\${vcs.url}") uri: String, @Value("\${vcs.access_token}") accessToken: String) =
        WebClient.builder()
            .baseUrl("https://$uri")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_VND_GITHUB_JSON)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .build()

    @Bean
    fun githubClient(webClient: WebClient) = GithubClient(webClient)

    @Bean
    fun githubRetrieveService(githubClient: GithubClient, repositoryMapper: RepositoryMapper) =
        GithubRetrieveService(githubClient, repositoryMapper)
}

private const val APPLICATION_VND_GITHUB_JSON = "application/vnd.github+json"
