package nom.brunokarpo.subscriptions.application.customer.exceptions

import nom.brunokarpo.subscriptions.application.usecases.ApplicationException

class CustomerUniqueEmailException(
	email: String
) : ApplicationException("Customer with email '$email' already exists!")