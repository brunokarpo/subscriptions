package nom.brunokarpo.subscriptions.infra.database.mappers

import nom.brunokarpo.subscriptions.domain.customer.subscriptions.Subscription
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.SubscriptionStatus
import nom.brunokarpo.subscriptions.domain.product.ProductId
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.util.UUID

class SubscriptionRowMapper : RowMapper<Subscription> {
    override fun mapRow(
        rs: ResultSet,
        rowNum: Int,
    ): Subscription =
        Subscription.from(
            productId = ProductId.from(rs.getObject("product_id", UUID::class.java)),
            productName = rs.getString("name"),
            status = rs.getString("status")?.let { SubscriptionStatus.valueOf(it) } ?: SubscriptionStatus.REQUESTED,
        )
}
