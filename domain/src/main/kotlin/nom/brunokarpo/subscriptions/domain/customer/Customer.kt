package nom.brunokarpo.subscriptions.domain.customer

import nom.brunokarpo.subscriptions.domain.common.AggregateRoot

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

		fun create(name: String, email: String): Customer = create(id = CustomerId.unique(), name = name, email = email)
	}
}