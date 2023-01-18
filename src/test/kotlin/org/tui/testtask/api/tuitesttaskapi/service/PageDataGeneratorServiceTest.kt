package org.tui.testtask.api.tuitesttaskapi.service

import org.junit.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import reactor.test.publisher.TestPublisher

class PageDataGeneratorServiceTest {

    private val generator: PageDataGeneratorService<String, String> = PageDataGeneratorService()

    @Test
    fun shouldGenerateFluxFromCollections() {
        val page1 = TestPublisher.createCold<String>()
            .emit("test1")
            .emit("test2")
            .flux()
        val page2 = TestPublisher.createCold<String>()
            .emit("test3")
            .emit("test4")
            .flux()
        val page3 = Flux.empty<String>()

        val map = HashMap<Int, Flux<String>>()
        map[1] = page1
        map[2] = page2
        map[3] = page3

        var flux = generator.generateDataFlux("test") {
            _, page -> map[page.get()]!!
        }

        StepVerifier.create(flux)
            .expectNextCount(4)
            .verifyComplete()
    }
}