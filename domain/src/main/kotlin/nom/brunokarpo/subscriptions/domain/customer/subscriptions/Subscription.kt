package nom.brunokarpo.subscriptions.domain.customer.subscriptions

import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId

class Subscription private constructor(
    val productId: ProductId,
    val productName: String,
) {
    companion object {
        fun to(product: Product): Subscription = Subscription(productId = product.id, productName = product.name)
    }
}
