package nom.brunokarpo.subscriptions.domain.customer.exceptions

import nom.brunokarpo.subscriptions.domain.common.DomainException

class SubscriptionStatusUnknownException(
    statusName: String,
    cause: Throwable? = null,
) : DomainException("Subscription Status unknown: $statusName", cause)
