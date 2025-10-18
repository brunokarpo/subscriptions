package nom.brunokarpo.subscriptions.domain.customer

import java.time.ZonedDateTime

class ActivationKey private constructor(
    customer: Customer,
) {
    companion object {
        fun of(customer: Customer): ActivationKey = ActivationKey(customer)
    }

    val email: String = customer.email
    val products: Set<String> = customer.subscriptions.map { it.productName }.toSet()
    val validUntil: ZonedDateTime = customer.activeUntil!!
}
