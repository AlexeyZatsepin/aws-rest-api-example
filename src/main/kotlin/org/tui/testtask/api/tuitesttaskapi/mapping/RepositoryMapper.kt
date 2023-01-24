package org.tui.testtask.api.tuitesttaskapi.mapping

import org.mapstruct.Builder
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy
import org.tui.testtask.api.tuitesttaskapi.client.dto.BranchesResponse
import org.tui.testtask.api.tuitesttaskapi.client.dto.RepositoryResponse
import org.tui.testtask.api.tuitesttaskapi.model.Branch
import org.tui.testtask.api.tuitesttaskapi.model.Repository

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