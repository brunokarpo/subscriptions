package nom.brunokarpo.subscriptions.infra.database.repositories

import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class CustomerDatabaseRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : CustomerRepository {
    override suspend fun save(aggregate: Customer) {
        TODO("Not yet implemented")

    }

    override suspend fun findById(id: CustomerId): Customer? = TODO()

    override suspend fun findByEmail(email: String): Customer? = TODO()
}
