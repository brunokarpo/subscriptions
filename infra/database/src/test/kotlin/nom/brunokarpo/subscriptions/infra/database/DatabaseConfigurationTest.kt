package nom.brunokarpo.subscriptions.infra.database

import nom.brunokarpo.subscriptions.infra.database.configurations.ContainersInitializer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import java.nio.charset.Charset

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.NONE,
	classes = [DatabaseConfiguration::class]
)
@ContextConfiguration(initializers = [ContainersInitializer::class])
open class DatabaseConfigurationTest {

	@Autowired
	private lateinit var jdbcTemplate: JdbcTemplate

	@Test
	fun initContexts() {
	}

	@BeforeEach
	fun setUp() {
		val sql = this.javaClass.getResource("/clear_tables.sql")
		if (sql != null) {
			jdbcTemplate.execute(sql.readText())
		}
		println("Data cleared from tables")
	}

	protected suspend fun loadDatabase(resourceName: String) {
		val sql = this.javaClass.getResource(resourceName)
		if (sql != null) {
			jdbcTemplate.execute(sql.readText())
		}
		println("Data loaded from $resourceName")
	}
}