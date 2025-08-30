package nom.brunokarpo.subscriptions.domain.customer.subscriptions

import nom.brunokarpo.subscriptions.domain.product.Product

internal class Subscriptions {
	private val products: MutableSet<Subscription> = mutableSetOf()

	fun add(product: Product) {
		products.add(Subscription(product))
	}

	fun productNames(): Set<String> {
		return products.map { it.productName }.toSet()
	}
}