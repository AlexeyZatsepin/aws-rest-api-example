package org.tui.testtask.api.tuitesttaskapi.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Model for a VCS repository")
data class Repository (
    @field:Schema(
        description = "repository name",
        example = "test-repo",
        type = "String"
    )
    @JsonProperty("name") var name: String? = null,
    @field:Schema(
        description = "login of VCS repository owner",
        example = "OZatsepin",
        type = "String"
    )
    @JsonProperty("owner") var owner: String? = null,
    @field:ArraySchema(
        schema = Schema(implementation = Branch::class)
    )
    @JsonProperty("branches") var branches: List<Branch>? = null
)

@Schema(description = "Model for a branch of VCS repository")
data class Branch (
    @field:Schema(
        description = "name of branch",
        example = "main",
        type = "String"
    )
    @JsonProperty("name") var name: String? = null,
    @field:Schema(
        description = "SHA of last commit in this branch",
        example = "12345",
        type = "String"
    )
    @JsonProperty("sha") var sha: String? = null
)