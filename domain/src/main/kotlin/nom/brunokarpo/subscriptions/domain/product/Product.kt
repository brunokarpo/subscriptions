package nom.brunokarpo.subscriptions.domain.product

import nom.brunokarpo.subscriptions.domain.common.AggregateRoot
import nom.brunokarpo.subscriptions.domain.product.events.ProductCreated

class Product private constructor(override val id: ProductId, val name: String) : AggregateRoot() {

	companion object {
		fun create(
            productId: ProductId = ProductId.unique(),
            name: String,
            emitEvent: Boolean = true
        ): Product {
			val product = Product(productId, name)

            if (emitEvent) {
                product.recordEvent(
                    ProductCreated(domainId = product.id, name = product.name)
                )
            }

			return product
		}
	}

}