package nom.brunokarpo.subscriptions.domain.common

import java.time.ZonedDateTime

interface DomainEvent {

	val domainId: Identifier<*>
	val occurredOn: ZonedDateTime

}