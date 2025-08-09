package nom.brunokarpo.subscriptions.domain.product

import nom.brunokarpo.subscriptions.domain.common.AggregateRoot

class Product private constructor(productId: ProductId, val name: String) : AggregateRoot() {

	override val id = productId.id.toString()

	companion object {
		fun create(name: String): Product {
			val product = Product(ProductId.unique(), name)

			product.recordEvent(
				ProductCreated(domainId = product.id, name = product.name)
			)

			return product
		}

		fun create(uuid: String, name: String): Product = Product(ProductId(uuid), name)
	}

}