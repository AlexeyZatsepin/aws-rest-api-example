package org.tui.testtask.api.tuitesttaskapi.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import reactor.core.publisher.Mono


@ControllerAdvice
class CustomRestExceptionHandler {

    @ExceptionHandler(GithubResourceNotFoundException::class)
    fun handleNodataFoundException(ex: GithubResourceNotFoundException): Mono<ResponseEntity<Any>> {
        val body: MutableMap<String, Any> = LinkedHashMap()
        body["status"] = HttpStatus.NOT_FOUND.value()
        body["message"] = ex.message
        return Mono.just(ResponseEntity(body, HttpStatus.NOT_FOUND))
    }

}