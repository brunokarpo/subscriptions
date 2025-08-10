package nom.brunokarpo.subscriptions.domain.customer.events

import nom.brunokarpo.subscriptions.domain.common.DomainEvent
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.product.ProductId
import java.time.ZonedDateTime

class ProductSubscribed(
	override val domainId: CustomerId,
	override val occurredOn: ZonedDateTime = ZonedDateTime.now(),
	val productId: ProductId
) : DomainEvent {
}