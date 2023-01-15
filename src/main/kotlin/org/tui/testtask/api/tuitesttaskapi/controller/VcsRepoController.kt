package org.tui.testtask.api.tuitesttaskapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.*
import org.tui.testtask.api.tuitesttaskapi.model.Repository
import org.tui.testtask.api.tuitesttaskapi.service.RetrieveService
import reactor.core.publisher.Flux


@RestController
@RequestMapping("v1/repos")
class VcsRepoController(
    private val service: RetrieveService
) {

    @Operation(
        summary = "Get list of repositories for provided username in VCS",
        description = "Returns 200 if successful"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful Operation"),
            ApiResponse(responseCode = "404", description = "Provided user does not exist"),
            ApiResponse(responseCode = "406", description = "Not Acceptable"),
        ]
    )
    @GetMapping("/{username}")
    fun getAllRepositories(
        @PathVariable username: String,
        @RequestParam(value = "page", required = false, defaultValue = "1") page: Int,
        @RequestParam(value = "size", required = false, defaultValue = "30") size: Int
    ): Flux<Repository> =
        service.retrieveRepositories(username, page, size)
}