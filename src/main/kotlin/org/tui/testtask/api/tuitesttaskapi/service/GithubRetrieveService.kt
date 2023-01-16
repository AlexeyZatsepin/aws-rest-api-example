package org.tui.testtask.api.tuitesttaskapi.service

import org.tui.testtask.api.tuitesttaskapi.client.GithubClient
import org.tui.testtask.api.tuitesttaskapi.mapping.RepositoryMapper
import reactor.core.scheduler.Schedulers

class GithubRetrieveService(
    private val githubClient: GithubClient,
    private val mapper: RepositoryMapper
) : RetrieveService {

    override fun retrieveRepositories(username: String, page: Int, size: Int) =
        githubClient.getAllRepositories(username, page, size)
            .log()
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .filter { !it.fork }
            .flatMap { res ->
                githubClient.getAllBranches(username, res.name)
                    .collectList()
                    .map {
                        mapper.map(res, it)
                    }
            }
            .sequential()
}