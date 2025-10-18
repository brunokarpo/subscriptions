package nom.brunokarpo.subscriptions.infra.database.configurations.containers

import org.flywaydb.core.Flyway
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container

private const val DATABASE_VERSION = "postgres:17-alpine"
private const val PORT = 5432
private const val DATABASE_NAME = "subscriptions-db"
private const val USERNAME = "drugstore-app"
private const val PASSWORD = USERNAME
private const val MIGRATION_FILES = "../scripts/db/migrations"

object PostgresContainer {
    @Container
    val container: JdbcDatabaseContainer<*> =
        PostgreSQLContainer<Nothing>(DATABASE_VERSION).apply {
            withExposedPorts(PORT)
            withDatabaseName(DATABASE_NAME)
            withUsername(USERNAME)
            withPassword(PASSWORD)
            withReuse(false)
        }

    fun start() {
        container.apply {
            start()
            waitingFor(Wait.forListeningPort())
        }
    }

    fun migrate() {
        Flyway
            .configure()
            .dataSource(container.jdbcUrl, container.username, container.password)
            .locations("filesystem:$MIGRATION_FILES")
            .configuration(mapOf("flyway.postgresql.transactional.lock" to "false"))
            .load()
            .migrate()
    }

    val environment: Map<String, String>
        get() {
            return mapOf(
                "spring.datasource.url" to container.jdbcUrl,
                "spring.datasource.username" to container.username,
                "spring.datasource.password" to container.password,
                "spring.hibernate.jpa.hibernate.ddl-auto" to "none",
            )
        }
}
