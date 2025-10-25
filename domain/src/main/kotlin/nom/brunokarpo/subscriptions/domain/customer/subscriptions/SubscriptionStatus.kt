package nom.brunokarpo.subscriptions.domain.customer.subscriptions

import nom.brunokarpo.subscriptions.domain.customer.exceptions.SubscriptionStatusUnknownException

enum class SubscriptionStatus {
    REQUESTED;

    companion object {
        fun of(status: String): SubscriptionStatus = try {
            valueOf(status.uppercase())
        } catch (e: IllegalArgumentException) {
            throw SubscriptionStatusUnknownException(status, e)
        }
    }
}
