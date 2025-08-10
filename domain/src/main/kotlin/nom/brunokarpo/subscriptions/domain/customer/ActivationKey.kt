package nom.brunokarpo.subscriptions.domain.customer

import java.time.ZonedDateTime

class ActivationKey(
	val email: String,
	val products: Set<String>,
	val validUntil: ZonedDateTime
)