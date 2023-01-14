package org.tui.testtask.api.tuitesttaskapi.service

import org.springframework.stereotype.Component
import org.tui.testtask.api.tuitesttaskapi.client.GithubClient
import org.tui.testtask.api.tuitesttaskapi.mapping.RepositoryMapper
import reactor.core.scheduler.Schedulers

@Component
class RetrieveService(
    private val githubClient: GithubClient,
    private val mapper: RepositoryMapper) {

    fun retrieveRepositories(org: String) {
        val repoResponse = githubClient.getAllRepositories(org)
            .publishOn(Schedulers.boundedElastic())
            .doOnNext { res ->
                println(res)
                println("---------------------")
                val branchesResponse = githubClient.getAllBranches(org, res.name!!)
                    .doOnNext {
                        println("---------${res.name}------------")
                        println(it)
                    }
                    .subscribe()
                mapper.map(res, arrayListOf())
            }
            .subscribe()


    }

}