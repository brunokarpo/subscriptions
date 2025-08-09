package nom.brunokarpo.subscriptions.infra.database.configurations

import nom.brunokarpo.subscriptions.infra.database.configurations.containers.PostgresContainer
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

class ContainersInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
	override fun initialize(applicationContext: ConfigurableApplicationContext) {
		PostgresContainer.start()
		PostgresContainer.migrate()
		TestPropertyValues
			.of(PostgresContainer.environment.map { (k, v) -> "$k=$v" })
			.applyTo(applicationContext.environment)
	}
}