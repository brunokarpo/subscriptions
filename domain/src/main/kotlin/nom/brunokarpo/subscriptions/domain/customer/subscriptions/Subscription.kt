package nom.brunokarpo.subscriptions.domain.customer.subscriptions

import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId

class Subscription(
	val productId: ProductId,
	val productName: String
) {
	constructor(product: Product): this(
			productId = product.id,
			productName = product.name
	)
}