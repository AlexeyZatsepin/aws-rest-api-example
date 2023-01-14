package org.tui.testtask.api.tuitesttaskapi.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Repository (
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("owner") var owner: String? = null,
    @JsonProperty("branches") var branches: List<Branch>? = null
)

data class Branch (
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("sha") var sha: String? = null
)