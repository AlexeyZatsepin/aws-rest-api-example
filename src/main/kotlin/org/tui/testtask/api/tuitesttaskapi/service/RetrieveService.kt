package org.tui.testtask.api.tuitesttaskapi.service

import org.tui.testtask.api.tuitesttaskapi.model.Repository
import reactor.core.publisher.Flux

interface RetrieveService {
    fun retrieveRepositories(username: String, page: Int, size: Int) : Flux<Repository>
}