package org.tui.testtask.api.tuitesttaskapi.error.dto

data class ApiError (
    val status: Int,
    val message: String
)