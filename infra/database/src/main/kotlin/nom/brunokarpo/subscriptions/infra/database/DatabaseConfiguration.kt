package nom.brunokarpo.subscriptions.infra.database

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableAutoConfiguration
@ComponentScan
@Configuration
@EnableJpaRepositories
class DatabaseConfiguration