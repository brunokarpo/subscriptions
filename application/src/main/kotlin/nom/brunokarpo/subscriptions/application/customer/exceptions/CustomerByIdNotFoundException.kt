package nom.brunokarpo.subscriptions.application.customer.exceptions

import nom.brunokarpo.subscriptions.domain.customer.CustomerId

class CustomerByIdNotFoundException(
	customerId: CustomerId
) : Exception("Customer with id '$customerId' does not exists!")