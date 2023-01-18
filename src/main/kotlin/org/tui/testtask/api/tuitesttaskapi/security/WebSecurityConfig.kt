package org.tui.testtask.api.tuitesttaskapi.security

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@EnableWebFluxSecurity
@Configuration
class WebSecurityConfig {

    @Bean
    @ConditionalOnProperty(name = ["security.use-cognito-token"], havingValue = "true")
    fun springSecurityFilterChain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain {
        http.cors().and().csrf().disable()
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET,"/actuator/**").permitAll()
            .pathMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v3/api-docs/**",
                "/webjars/**"
            ).permitAll()
            .anyExchange()
            .authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt()
        return http.build()
    }

    @Bean
    @ConditionalOnProperty(name = ["security.use-cognito-token"], havingValue = "false")
    fun springDisabledSecurityFilterChain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain {
        http.cors().and().csrf().disable()
            .authorizeExchange()
            .anyExchange()
            .permitAll()
        return http.build()
    }
}