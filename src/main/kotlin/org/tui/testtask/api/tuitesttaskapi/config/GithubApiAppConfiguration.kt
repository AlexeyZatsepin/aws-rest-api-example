package org.tui.testtask.api.tuitesttaskapi.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.tui.testtask.api.tuitesttaskapi.client.GithubClient
import org.tui.testtask.api.tuitesttaskapi.mapping.RepositoryMapper
import org.tui.testtask.api.tuitesttaskapi.service.GithubRepositoryService
import org.tui.testtask.api.tuitesttaskapi.service.PageDataGeneratorService
import java.util.*


@Configuration
@ConditionalOnProperty(name = ["vcs.type"], havingValue = "github")
class GithubApiAppConfiguration {
    @Bean
    @Qualifier("githubWebClient")
    fun githubWebClient(
        @Value("\${vcs.url}") uri: String,
        @Value("\${vcs.protocol}") protocol: String,
        @Value("\${vcs.access_token}") accessToken: String) =
        WebClient.builder()
            .baseUrl("$protocol://$uri")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_VND_GITHUB_JSON)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .build()

    @Bean
    fun githubClient(githubWebClient: WebClient) = GithubClient(githubWebClient)

    @Bean
    fun githubRetrieveService(githubClient: GithubClient, repositoryMapper: RepositoryMapper) =
        GithubRepositoryService(githubClient, repositoryMapper, PageDataGeneratorService())
}

private const val APPLICATION_VND_GITHUB_JSON = "application/vnd.github+json"
