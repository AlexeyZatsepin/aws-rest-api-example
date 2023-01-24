package org.tui.testtask.api.tuitesttaskapi.client.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class RepositoryResponse(
    var id: Int? = null,
    var name: String,
    @JsonProperty("full_name") var fullName: String? = null,
    var private: Boolean? = null,
    var owner: Owner? = null,
    var description: String? = null,
    var fork: Boolean,
    var url: String? = null,
)



data class Owner(
    var login: String,
    var id: Int? = null,
    var url: String? = null,
    var type: String? = null,
)