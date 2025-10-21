package nom.brunokarpo.subscriptions.infra.database.jpa.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.Subscription
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.SubscriptionStatus
import nom.brunokarpo.subscriptions.domain.product.ProductId
import java.util.UUID

@Embeddable
class SubscriptionId {

    @Column(name = "customer_id", nullable = false)
    var customerId: UUID? = null

    @Column(name = "product_id", nullable = false)
    var productId: UUID? = null
}

@Entity(name = "subscriptions")
class SubscriptionMapper {

    @EmbeddedId
    lateinit var id: SubscriptionId

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("customerId")
    @JoinColumn(name = "customer_id")
    lateinit var customer: CustomerMapper

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    lateinit var product: ProductMapper


    lateinit var status: String

    companion object {
        fun from(customer: CustomerMapper, subscription: Subscription): SubscriptionMapper {
            return SubscriptionMapper().also {
                it.customer = customer
                it.product = ProductMapper().also { p -> p.id = subscription.productId.value() }
                it.status = subscription.status.name
            }
        }
    }

    fun toDomain(): Subscription = Subscription.from(
        productId = ProductId.from(product.id),
        productName = product.name,
        status = SubscriptionStatus.valueOf(status)
    )
}