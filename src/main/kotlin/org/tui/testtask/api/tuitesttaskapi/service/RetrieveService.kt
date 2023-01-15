package org.tui.testtask.api.tuitesttaskapi.service

import org.springdoc.core.converters.models.Pageable
import org.tui.testtask.api.tuitesttaskapi.model.Repository
import reactor.core.publisher.Flux

interface RetrieveService {
    fun retrieveRepositories(username: String, pageable: Pageable? = null) : Flux<Repository>
}