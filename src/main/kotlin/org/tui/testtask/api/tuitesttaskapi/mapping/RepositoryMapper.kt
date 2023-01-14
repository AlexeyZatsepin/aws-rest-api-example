package org.tui.testtask.api.tuitesttaskapi.mapping

import org.mapstruct.*
import org.springframework.stereotype.Component
import org.tui.testtask.api.tuitesttaskapi.model.Branch
import org.tui.testtask.api.tuitesttaskapi.model.Repository
import org.tui.testtask.api.tuitesttaskapi.model.dto.BranchesResponse
import org.tui.testtask.api.tuitesttaskapi.model.dto.RepositoryResponse

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.WARN,
    builder = Builder(disableBuilder = true)
)
interface RepositoryMapper {

    @Mapping(source = "repo.name", target = "name")
    @Mapping(source = "repo.owner.login", target = "owner")
    @Mapping(source = "branches", target = "branches")
    fun map(repo: RepositoryResponse, branches: List<BranchesResponse>): Repository

    @Mapping(source = "name", target = "name")
    @Mapping(source = "commit.sha", target = "sha")
    fun map(branch: BranchesResponse): Branch
}