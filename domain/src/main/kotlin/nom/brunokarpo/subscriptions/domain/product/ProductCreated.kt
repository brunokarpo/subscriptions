package nom.brunokarpo.subscriptions.domain.product

import nom.brunokarpo.subscriptions.domain.common.DomainEvent
import java.time.ZonedDateTime

class ProductCreated(
	override val domainId: String,
	override val occurredOn: ZonedDateTime = ZonedDateTime.now(),
	val name: String
) : DomainEvent