package nom.brunokarpo.subscriptions.application.customer.exceptions

class CustomerUniqueEmailException(email: String) : Exception("Customer with email '$email' already exists!")