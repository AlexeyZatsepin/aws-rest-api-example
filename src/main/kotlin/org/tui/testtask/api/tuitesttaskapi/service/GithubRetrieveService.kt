package org.tui.testtask.api.tuitesttaskapi.service

import org.springdoc.core.converters.models.Pageable
import org.tui.testtask.api.tuitesttaskapi.client.GithubClient
import org.tui.testtask.api.tuitesttaskapi.mapping.RepositoryMapper
import reactor.core.scheduler.Schedulers

class GithubRetrieveService(
    private val githubClient: GithubClient,
    private val mapper: RepositoryMapper
) : RetrieveService {

    override fun retrieveRepositories(username: String, pageable: Pageable?) =
        githubClient.getAllRepositories(username)
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