package nom.brunokarpo.subscriptions.domain.product

import nom.brunokarpo.subscriptions.domain.common.AggregateRoot
import nom.brunokarpo.subscriptions.domain.product.events.ProductCreated

class Product private constructor(override val id: ProductId, val name: String) : AggregateRoot() {

	companion object {
		fun create(name: String): Product {
			val product = Product(ProductId.unique(), name)

			product.recordEvent(
				ProductCreated(domainId = product.id, name = product.name)
			)

			return product
		}

		fun create(productId: ProductId, name: String): Product = Product(id = productId, name =  name)
	}

}