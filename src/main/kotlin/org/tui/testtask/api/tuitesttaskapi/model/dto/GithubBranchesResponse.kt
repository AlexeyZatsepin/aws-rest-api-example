package org.tui.testtask.api.tuitesttaskapi.model.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class BranchesResponse(
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("commit") var commit: Commit? = Commit(),
    @JsonProperty("protected") var protected: Boolean? = null
)

data class Commit(
    @JsonProperty("sha") var sha: String? = null,
    @JsonProperty("url") var url: String? = null
)