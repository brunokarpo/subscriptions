package nom.brunokarpo.subscriptions.infra.database

import nom.brunokarpo.subscriptions.infra.database.configurations.ContainersInitializer
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.NONE,
	classes = [DatabaseConfiguration::class]
)
@ContextConfiguration(initializers = [ContainersInitializer::class])
@SqlGroup(
	Sql(scripts = ["/clear_tables.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
)
class DatabaseConfigurationTest {

	@Test
	fun initContexts() {
	}
}