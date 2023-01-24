package org.tui.testtask.api.tuitesttaskapi.service

import org.tui.testtask.api.tuitesttaskapi.model.Repository
import reactor.core.publisher.Flux

interface RepositoryService {
    fun retrieveRepositories(username: String, includeForks: Boolean, page: Int, size: Int) : Flux<Repository>
}