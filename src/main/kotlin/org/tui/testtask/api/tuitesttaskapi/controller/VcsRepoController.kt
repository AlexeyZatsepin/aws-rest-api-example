package org.tui.testtask.api.tuitesttaskapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springdoc.core.annotations.ParameterObject
import org.springdoc.core.converters.models.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.tui.testtask.api.tuitesttaskapi.model.Repository
import org.tui.testtask.api.tuitesttaskapi.service.RetrieveService
import reactor.core.publisher.Flux


@RestController
@RequestMapping("v1/repos")
class VcsRepoController(
    private val service: RetrieveService
) {

    @Operation(summary = "Get list of repositories for provided username in VCS", description = "Returns 200 if successful")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful Operation"),
            ApiResponse(responseCode = "404", description = "Provided user does not exist"),
            ApiResponse(responseCode = "406", description = "Not Acceptable"),
        ]
    )
    @GetMapping("/{username}")
    fun getAllRepositories(@PathVariable username: String, @ParameterObject pageable: Pageable): Flux<Repository> =
        service.retrieveRepositories(username, pageable)
}