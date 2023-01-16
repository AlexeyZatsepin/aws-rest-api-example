package org.tui.testtask.api.tuitesttaskapi.error

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.NotAcceptableStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@ControllerAdvice
class CustomRestExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(GithubResourceNotFoundException::class)
    fun handleNodataFoundException(ex: GithubResourceNotFoundException): Mono<ResponseEntity<Any>> {
        val body: MutableMap<String, Any> = LinkedHashMap()
        body["status"] = HttpStatus.NOT_FOUND.value()
        body["message"] = ex.message
        return Mono.just(ResponseEntity(body, HttpStatus.NOT_FOUND))
    }

    override fun handleNotAcceptableStatusException(
        ex: NotAcceptableStatusException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        val body: MutableMap<String, Any> = LinkedHashMap()
        body["status"] = status.value()
        body["message"] = "$ex.reason ($ex.body.detail)"
        return Mono.just(ResponseEntity(body, status))
    }

}