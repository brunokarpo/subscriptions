package nom.brunokarpo.subscriptions.application.customer.exceptions

import nom.brunokarpo.subscriptions.application.usecases.ApplicationException

class CustomerNotExistsException(
    customerEmail: String,
) : ApplicationException("Customer with email '$customerEmail' does not exists!")
