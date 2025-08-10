package nom.brunokarpo.subscriptions.domain.customer

import nom.brunokarpo.subscriptions.domain.common.Identifier
import java.util.UUID

class CustomerId private constructor(
	id: UUID
) : Identifier<UUID>(id = id) {

	companion object {
		fun unique(): CustomerId = CustomerId(UUID.randomUUID())
		fun from(uuid: UUID): CustomerId = CustomerId(uuid)
	}
}