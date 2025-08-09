package nom.brunokarpo.subscriptions.infra.database

import nom.brunokarpo.subscriptions.infra.database.configurations.ContainersInitializer
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.NONE,
	classes = [DatabaseConfiguration::class]
)
@ContextConfiguration(initializers = [ContainersInitializer::class])
class DatabaseConfigurationTest {

	@Test
	fun initContexts() {
	}
}