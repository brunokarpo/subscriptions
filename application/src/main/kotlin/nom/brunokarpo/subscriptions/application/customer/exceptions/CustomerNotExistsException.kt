package nom.brunokarpo.subscriptions.application.customer.exceptions

class CustomerNotExistsException(
	customerEmail: String
) : Exception("Customer with email '$customerEmail' does not exists!")