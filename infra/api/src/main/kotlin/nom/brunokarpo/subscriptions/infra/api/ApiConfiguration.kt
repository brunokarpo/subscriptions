package nom.brunokarpo.subscriptions.infra.api

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.EnableWebFlux

@EnableAutoConfiguration
@ComponentScan
@Configuration
@EnableWebFlux
class ApiConfiguration
