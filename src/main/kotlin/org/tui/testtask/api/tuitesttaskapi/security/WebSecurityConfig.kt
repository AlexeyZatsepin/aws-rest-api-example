package org.tui.testtask.api.tuitesttaskapi.security

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@EnableWebFluxSecurity
class WebSecurityConfig {

    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain {
        http.cors().and().csrf().disable()
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET,"/actuator/**").permitAll()
            .pathMatchers("/openapi", "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs", "/v2/api-docs").permitAll()
            .pathMatchers("/v1/**").authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt()
        return http.build()
    }
}