package org.tui.testtask.api.tuitesttaskapi.service

import org.tui.testtask.api.tuitesttaskapi.client.GithubClient
import org.tui.testtask.api.tuitesttaskapi.client.dto.BranchesResponse
import org.tui.testtask.api.tuitesttaskapi.mapping.RepositoryMapper
import reactor.core.scheduler.Schedulers

class GithubRepositoryService(
    private val githubClient: GithubClient,
    private val mapper: RepositoryMapper,
    private val pageDataGeneratorService: PageDataGeneratorService<Pair<String,String>, BranchesResponse>
) : RepositoryService {

    override fun retrieveRepositories(username: String, includeForks: Boolean, page: Int, size: Int) =
        githubClient.getAllRepositories(username, page, size)
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .filter { includeForks || !it.fork }
            .flatMap { res ->
                pageDataGeneratorService.generateDataFlux(
                    Pair(username, res.name)
                ) { pair, page -> githubClient.getAllBranches(pair.first, pair.second, page.get()) }
                    .collectList()
                    .map {
                        mapper.map(res, it)
                    }
            }
            .sequential()
}