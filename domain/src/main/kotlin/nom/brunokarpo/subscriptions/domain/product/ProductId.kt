package nom.brunokarpo.subscriptions.domain.product

import nom.brunokarpo.subscriptions.domain.common.Identifier
import java.util.UUID

class ProductId private constructor(
	id: UUID
): Identifier<UUID>(id) {

	companion object {
		fun unique(): ProductId = ProductId(UUID.randomUUID())
		fun from(id: String): ProductId = from(UUID.fromString(id))
		fun from(uuid: UUID): ProductId = ProductId(uuid)
	}

}
