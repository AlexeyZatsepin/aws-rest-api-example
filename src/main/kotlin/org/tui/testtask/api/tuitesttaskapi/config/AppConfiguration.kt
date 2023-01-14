package org.tui.testtask.api.tuitesttaskapi.config

import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import org.tui.testtask.api.tuitesttaskapi.client.GithubClient
import org.tui.testtask.api.tuitesttaskapi.mapping.RepositoryMapper
import reactor.core.publisher.Mono
import java.util.*
import java.util.function.Consumer


private const val APPLICATION_VND_GITHUB_JSON = "application/vnd.github+json"

@Configuration
class AppConfiguration {

    @Bean
    @ConditionalOnProperty(name = ["vcs.type"], havingValue = "github")
    fun githubWebClient(@Value("\${vcs.url}") uri: String) = WebClient.builder()
        .baseUrl("https://$uri")
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_VND_GITHUB_JSON)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer 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")
        .build()

    @Bean
    @ConditionalOnBean(name = ["githubWebClient"])
    fun githubClient(webClient: WebClient) = GithubClient(webClient)

    @Bean
    fun repositoryMapper(): RepositoryMapper = Mappers.getMapper(RepositoryMapper::class.java)

    private fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { clientRequest: ClientRequest ->
            println("Request: ${clientRequest.method()} ${clientRequest.url()}")
            clientRequest.headers()
                .forEach { name: String?, values: List<String?> ->
                    values.forEach(
                        Consumer<String?> { value: String? ->
                            println("${name}=${value}")
                        })
                }
            Mono.just(clientRequest)
        }
    }
}