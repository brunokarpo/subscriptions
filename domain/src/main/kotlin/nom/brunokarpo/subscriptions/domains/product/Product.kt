package nom.brunokarpo.subscriptions.domains.product

import java.util.UUID

class Product private constructor(val uuid: UUID, val name: String) {

	companion object {
		fun create(uuid: UUID, name: String): Product = Product(uuid, name)
	}

}