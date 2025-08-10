package nom.brunokarpo.subscriptions.domain.customer.events

import nom.brunokarpo.subscriptions.domain.common.DomainEvent
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import java.time.ZonedDateTime

class CustomerCreated(
	override val domainId: CustomerId,
	override val occurredOn: ZonedDateTime = ZonedDateTime.now(),
	val name: String,
	val email: String
) : DomainEvent {

	constructor(customer: Customer): this(
			domainId = customer.id,
			name = customer.name,
			email = customer.email
	)
}