package nom.brunokarpo.subscriptions.domain.customer

import nom.brunokarpo.subscriptions.domain.common.AggregateRoot
import nom.brunokarpo.subscriptions.domain.customer.Customer.Companion.create
import nom.brunokarpo.subscriptions.domain.customer.events.CustomerActivated
import nom.brunokarpo.subscriptions.domain.customer.events.CustomerCreated
import nom.brunokarpo.subscriptions.domain.customer.events.ProductSubscribed
import nom.brunokarpo.subscriptions.domain.customer.exceptions.CustomerNotActiveException
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.Subscription
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.Subscriptions
import nom.brunokarpo.subscriptions.domain.product.Product
import java.time.ZonedDateTime

class Customer private constructor(
	override val id: CustomerId,
	val name: String,
	val email: String
) : AggregateRoot() {

	private val subscriptions: Subscriptions = Subscriptions()

	var active = false
		private set
	var activeUntil: ZonedDateTime? = null
		private set

	companion object {
		/**
		 * This method should only be used by the repository layer to reconstruct the entity without generate any domain event.
		 * For business logic purpose use #create(name: String, email: String) method
		 */
		fun create(
			id: CustomerId,
			name: String,
			email: String,
			active: Boolean = false,
			activeUntil: ZonedDateTime? = null,
			products: List<Product> = listOf()
		): Customer = Customer(
			id = id,
			name = name,
			email = email
		).also {
			it.active = active
			it.activeUntil = activeUntil

			products.map { product -> it.subscriptions.add(product) }
		}

		fun create(name: String, email: String): Customer {
			val customer = create(id = CustomerId.unique(), name = name, email = email)
			customer.recordEvent(CustomerCreated(customer))
			return customer
		}
	}

	fun subscribe(product: Product) {
		if (!this.active) {
			throw CustomerNotActiveException(customerId = this.id)
		}
		subscriptions.add(product)
		this.recordEvent(ProductSubscribed(domainId = this.id, productId = product.id))
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