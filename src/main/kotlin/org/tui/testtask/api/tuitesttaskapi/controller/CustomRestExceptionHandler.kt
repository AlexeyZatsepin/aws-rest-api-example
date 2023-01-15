package org.tui.testtask.api.tuitesttaskapi.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.NotAcceptableStatusException
import org.springframework.web.server.ServerWebExchange
import org.tui.testtask.api.tuitesttaskapi.model.ApiError
import reactor.core.publisher.Mono


@ControllerAdvice
class CustomRestExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleNotAcceptableStatusException(
        ex: NotAcceptableStatusException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        val apiError = ApiError(HttpStatus.NOT_ACCEPTABLE.value(), ex.localizedMessage)
        return Mono.just(ResponseEntity(apiError, HttpStatus.NOT_ACCEPTABLE))
    }
}