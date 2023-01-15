package org.tui.testtask.api.tuitesttaskapi.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Model for api errors")
data class ApiError(
    @field:Schema(
        description = "error code",
        example = "404",
        type = "Int"
    )
    val status: Int,
    @field:Schema(
        description = "description message",
        example = "REPOSITORY OBJECT NOT FOUND",
        type = "String"
    )
    val message: String)
