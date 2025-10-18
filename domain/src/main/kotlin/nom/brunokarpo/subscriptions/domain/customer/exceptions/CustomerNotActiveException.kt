package nom.brunokarpo.subscriptions.domain.customer.exceptions

import nom.brunokarpo.subscriptions.domain.common.DomainException
import nom.brunokarpo.subscriptions.domain.customer.CustomerId

class CustomerNotActiveException(
    customerId: CustomerId,
) : DomainException(message = "Customer with id: $customerId is not active!")
