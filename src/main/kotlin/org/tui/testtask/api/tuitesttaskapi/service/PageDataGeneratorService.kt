package org.tui.testtask.api.tuitesttaskapi.service

import reactor.core.publisher.Flux
import reactor.core.publisher.SynchronousSink
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.BiFunction


class PageDataGeneratorService<D,T> {

    fun generateDataFlux(
        data: D, generatorBase: BiFunction<D, AtomicInteger, Flux<T>>
    ): Flux<T> {
        val page = AtomicInteger(1)
        return Flux.generate { synchronousSink ->
            getNextPage(data, page, generatorBase, synchronousSink)
        }.flatMap {
            Flux.fromIterable(it)
        }
    }

    private fun getNextPage(
        data: D,
        page: AtomicInteger,
        generatorBase: BiFunction<D, AtomicInteger, Flux<T>>,
        synchronousSink: SynchronousSink<List<T>>
    ) {
        val collectedPage = generatorBase.apply(data, page)
            .collectList()
            .block()

        if (!collectedPage.isNullOrEmpty()) {
            page.incrementAndGet()
            synchronousSink.next(collectedPage)
        } else {
            synchronousSink.complete()
        }
    }
}