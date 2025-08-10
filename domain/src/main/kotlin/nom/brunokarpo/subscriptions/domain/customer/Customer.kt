package nom.brunokarpo.subscriptions.domain.customer

import nom.brunokarpo.subscriptions.domain.common.AggregateRoot
import nom.brunokarpo.subscriptions.domain.customer.events.CustomerActivated
import nom.brunokarpo.subscriptions.domain.customer.events.CustomerCreated
import nom.brunokarpo.subscriptions.domain.customer.exceptions.CustomerNotActiveException
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.Subscriptions
import nom.brunokarpo.subscriptions.domain.product.Product
import java.time.ZonedDateTime

class Customer(
	override val id: CustomerId,
	val name: String,
	val email: String
) : AggregateRoot() {

	private val subscriptions: Subscriptions = Subscriptions()
	private var active = false
	private var activeUntil: ZonedDateTime? = null

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

	fun subscribe(product: Product) {
		subscriptions.add(product)
	}

	fun activate() {
		this.active = true
		this.activeUntil = ZonedDateTime.now().plusDays(30)
		this.recordEvent(CustomerActivated(domainId = this.id))
	}

	fun activationKey(): ActivationKey {
		if (!this.active) {
			throw CustomerNotActiveException(customerId = this.id)
		}
		return ActivationKey(
			email = this.email,
			products = this.subscriptions.productNames(),
			validUntil = this.activeUntil!!
		)
	}
}