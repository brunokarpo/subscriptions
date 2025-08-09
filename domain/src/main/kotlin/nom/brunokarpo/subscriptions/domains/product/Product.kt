package nom.brunokarpo.subscriptions.domains.product

import java.util.UUID

class Product private constructor(val id: UUID, val name: String) {

	companion object {
		fun create(name: String): Product = Product(UUID.randomUUID(), name)

		fun create(id: UUID, name: String): Product = Product(id, name)
	}

}