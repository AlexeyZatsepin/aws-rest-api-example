package org.tui.testtask.api.tuitesttaskapi.service

import org.tui.testtask.api.tuitesttaskapi.client.GithubClient
import org.tui.testtask.api.tuitesttaskapi.mapping.RepositoryMapper
import org.tui.testtask.api.tuitesttaskapi.model.dto.BranchesResponse
import reactor.core.scheduler.Schedulers

class GithubRetrieveService(
    private val githubClient: GithubClient,
    private val mapper: RepositoryMapper,
    private val pageDataGeneratorService: PageDataGeneratorService<Pair<String,String>,BranchesResponse>
) : RetrieveService {

    override fun retrieveRepositories(username: String, page: Int, size: Int) =
        githubClient.getAllRepositories(username, page, size)
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .filter { !it.fork }
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