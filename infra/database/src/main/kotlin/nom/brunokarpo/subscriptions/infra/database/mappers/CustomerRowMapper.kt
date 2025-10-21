package nom.brunokarpo.subscriptions.infra.database.mappers

import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.Subscription
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.ZonedDateTime
import java.util.UUID

class CustomerRowMapper(
    private val subscriptions: List<Subscription> = emptyList()
): RowMapper<Customer> {
    override fun mapRow(
        rs: ResultSet,
        rowNum: Int
    ): Customer {
        return Customer.Companion.create(
            id = CustomerId.Companion.from(rs.getObject("id", UUID::class.java)),
            name = rs.getString("name"),
            email = rs.getString("email"),
            active = rs.getBoolean("active"),
            activeUntil = rs.getTimestamp("active_until")?.let { ZonedDateTime.parse(it.toInstant().toString()) },
            subscription = subscriptions
        )
    }

}