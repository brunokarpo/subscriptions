package nom.brunokarpo.subscriptions.domain.customer

import nom.brunokarpo.subscriptions.domain.common.AggregateRoot
import nom.brunokarpo.subscriptions.domain.customer.events.CustomerCreated

class Customer(
	override val id: CustomerId,
	val name: String,
	val email: String
) : AggregateRoot() {

	companion object {
		fun create(id: CustomerId, name: String, email: String): Customer = Customer(
			id = id,
			name = name,
			email = email
		)

		fun create(name: String, email: String): Customer {
			val customer = create(id = CustomerId.unique(), name = name, email = email)
			customer.recordEvent(CustomerCreated(customer))
			return customer
		}
	}
}