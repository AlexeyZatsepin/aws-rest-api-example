package org.tui.testtask.api.tuitesttaskapi.runner

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.tui.testtask.api.tuitesttaskapi.service.RetrieveService


@Component
class TuiTestTaskRunner(val service: RetrieveService) : CommandLineRunner {

    override fun run(vararg args: String?) {
        service.retrieveRepositories("gyroflow")
    }
}