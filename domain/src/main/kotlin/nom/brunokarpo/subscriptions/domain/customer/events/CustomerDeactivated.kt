package nom.brunokarpo.subscriptions.domain.customer.events

import nom.brunokarpo.subscriptions.domain.common.DomainEvent
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import java.time.ZonedDateTime

class CustomerDeactivated(
    override val domainId: CustomerId,
    override val occurredOn: ZonedDateTime = ZonedDateTime.now(),
) : DomainEvent