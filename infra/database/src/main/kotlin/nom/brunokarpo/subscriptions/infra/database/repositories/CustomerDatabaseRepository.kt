package nom.brunokarpo.subscriptions.infra.database.repositories

import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.Subscription
import nom.brunokarpo.subscriptions.infra.database.mappers.CustomerRowMapper
import nom.brunokarpo.subscriptions.infra.database.mappers.SubscriptionRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.util.UUID

@Repository
class CustomerDatabaseRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) : CustomerRepository {
    override suspend fun save(aggregate: Customer) {
        val sql =
            """
            INSERT INTO customers (id, name, email, active, active_until, created_at)
            VALUES (:id, :name, :email, :active, :active_until, now())
            ON CONFLICT (id) DO UPDATE SET active = :active, active_until = :active_until
            """.trimIndent()
        val params =
            mapOf(
                "id" to aggregate.id.value(),
                "name" to aggregate.name,
                "email" to aggregate.email,
                "active" to aggregate.active,
                "active_until" to aggregate.activeUntil?.let { Timestamp.from(it.toInstant()) },
            )
        jdbcTemplate.update(sql, params)

        aggregate.subscriptions.forEach { subscription -> saveSubscription(aggregate, subscription) }
    }

    private suspend fun saveSubscription(
        customer: Customer,
        subscription: Subscription,
    ) {
        val sql =
            """
            INSERT INTO subscriptions (customer_id, product_id, status)
            VALUES (:customer_id, :product_id, :status)
            ON CONFLICT (customer_id, product_id) DO NOTHING
            """.trimIndent()
        val params =
            mapOf(
                "customer_id" to customer.id.value(),
                "product_id" to subscription.productId.value(),
                "status" to subscription.status.name,
            )
        jdbcTemplate.update(sql, params)
    }

    override suspend fun findById(id: CustomerId): Customer? {
        val subscriptions = loadSubscriptions(id.value())
        val sql =
            """
            SELECT id, name, email, active, active_until FROM customers WHERE id = :id
            """.trimIndent()
        val params = mapOf("id" to id.value())
        return jdbcTemplate.query(sql, params, CustomerRowMapper(subscriptions)).firstOrNull()
    }

    private suspend fun loadSubscriptions(customerId: UUID): List<Subscription> {
        val sql =
            """
            SELECT s.product_id, p.name, s.status FROM subscriptions s
            INNER JOIN products p ON p.id = s.product_id 
            WHERE customer_id = :customer_id
            """.trimIndent()
        val params = mapOf("customer_id" to customerId)
        return jdbcTemplate.query(sql, params, SubscriptionRowMapper())
    }

    override suspend fun findByEmail(email: String): Customer? {
        val subscriptions = loadSubscriptionsByEmail(email)
        val sql =
            """
            SELECT id, name, email, active, active_until FROM customers WHERE email = :email
            """.trimIndent()
        val params = mapOf("email" to email)
        return jdbcTemplate.query(sql, params, CustomerRowMapper(subscriptions)).firstOrNull()
    }

    private suspend fun loadSubscriptionsByEmail(customerEmail: String): List<Subscription> {
        val sql =
            """
            SELECT s.product_id, p.name, s.status FROM subscriptions s
            INNER JOIN customers c ON c.id = s.customer_id 
            INNER JOIN products p ON p.id = s.product_id
            WHERE c.email = :email
            """.trimIndent()
        val params = mapOf("email" to customerEmail)
        return jdbcTemplate.query(sql, params, SubscriptionRowMapper())
    }
}
