package org.tui.testtask.api.tuitesttaskapi.model.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class RepositoryResponse(
    @JsonProperty("id") var id: Int? = null,
    @JsonProperty("name") var name: String,
    @JsonProperty("full_name") var fullName: String? = null,
    @JsonProperty("private") var private: Boolean? = null,
    @JsonProperty("owner") var owner: Owner? = null,
    @JsonProperty("description") var description: String? = null,
    @JsonProperty("fork") var fork: Boolean,
    @JsonProperty("url") var url: String? = null,
)



data class Owner(
    @JsonProperty("login") var login: String,
    @JsonProperty("id") var id: Int? = null,
    @JsonProperty("url") var url: String? = null,
    @JsonProperty("type") var type: String? = null,
)