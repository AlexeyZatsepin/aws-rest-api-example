package org.tui.testtask.api.tuitesttaskapi

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition
class TuiTestTaskApiApplication

fun main(args: Array<String>) {
    runApplication<TuiTestTaskApiApplication>(*args)
}
