package nom.brunokarpo.subscriptions.domain.product.events

import nom.brunokarpo.subscriptions.domain.common.DomainEvent
import nom.brunokarpo.subscriptions.domain.product.ProductId
import java.time.ZonedDateTime

class ProductCreated(
	override val domainId: ProductId,
	override val occurredOn: ZonedDateTime = ZonedDateTime.now(),
	val name: String
) : DomainEvent