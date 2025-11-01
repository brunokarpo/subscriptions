package nom.brunokarpo.subscriptions.domain.customer.subscriptions

import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId

class Subscription private constructor(
    val productId: ProductId,
    val productName: String,
) {
    var status: SubscriptionStatus = SubscriptionStatus.REQUESTED
        private set

    companion object {
        fun to(product: Product): Subscription = Subscription(productId = product.id, productName = product.name)

        fun from(
            productId: ProductId,
            productName: String = "",
            status: SubscriptionStatus,
        ): Subscription {
            val subscription = Subscription(productId, productName)
            subscription.status = status
            return subscription
        }
    }

    fun activate() {
        this.status = SubscriptionStatus.ACTIVE
    }
}
