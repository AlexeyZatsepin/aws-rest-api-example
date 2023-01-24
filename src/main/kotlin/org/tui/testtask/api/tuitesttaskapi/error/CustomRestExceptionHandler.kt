package org.tui.testtask.api.tuitesttaskapi.error

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.tui.testtask.api.tuitesttaskapi.error.dto.ApiError


@RestControllerAdvice
class CustomRestExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GithubResourceNotFoundException::class)
    fun handleNodataFoundException(ex: GithubResourceNotFoundException) =
        ApiError(HttpStatus.NOT_FOUND.value(), ex.message)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException) =
        ApiError(HttpStatus.BAD_REQUEST.value(), ex.message!!)
}