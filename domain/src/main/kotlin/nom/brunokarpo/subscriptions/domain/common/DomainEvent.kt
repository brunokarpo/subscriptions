package nom.brunokarpo.subscriptions.domain.common

import java.time.ZonedDateTime

interface DomainEvent {

	val domainId: String
	val occurredOn: ZonedDateTime

}