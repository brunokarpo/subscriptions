package nom.brunokarpo.subscriptions.application.customer.exceptions

import nom.brunokarpo.subscriptions.application.usecases.ApplicationException
import nom.brunokarpo.subscriptions.domain.customer.CustomerId

class CustomerByIdNotFoundException(
	customerId: CustomerId
) : ApplicationException("Customer with id '$customerId' does not exists!")