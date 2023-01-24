package org.tui.testtask.api.tuitesttaskapi.client.dto

data class BranchesResponse(
    var name: String? = null,
    var commit: Commit? = Commit(),
    var protected: Boolean? = null
)

data class Commit(
    var sha: String? = null,
    var url: String? = null
)