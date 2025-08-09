package nom.brunokarpo.subscriptions.domain.product

import java.util.UUID

class ProductId(
	val id: UUID
) {
	companion object {
		fun unique(): ProductId = ProductId(UUID.randomUUID())
	}

	constructor(uuid: String): this(UUID.fromString(uuid))

	override fun toString(): String = id.toString()
}
