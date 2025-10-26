package nom.brunokarpo.subscriptions.domain.customer

import nom.brunokarpo.subscriptions.domain.common.AggregateRoot
import nom.brunokarpo.subscriptions.domain.customer.events.CustomerActivated
import nom.brunokarpo.subscriptions.domain.customer.events.CustomerCreated
import nom.brunokarpo.subscriptions.domain.customer.events.CustomerDeactivated
import nom.brunokarpo.subscriptions.domain.customer.events.ProductSubscribed
import nom.brunokarpo.subscriptions.domain.customer.exceptions.CustomerNotActiveException
import nom.brunokarpo.subscriptions.domain.customer.exceptions.SubscriptionNotFoundForProductIdException
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.Subscription
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.SubscriptionStatus
import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId
import java.time.ZonedDateTime
import kotlin.jvm.Throws

class Customer private constructor(
    override val id: CustomerId,
    val name: String,
    val email: String,
) : AggregateRoot() {
    private val _subscriptions = mutableSetOf<Subscription>()

    var active = false
        private set
    var activeUntil: ZonedDateTime? = null
        private set

    val subscriptions: List<Subscription>
        get() = _subscriptions.toList()

    companion object {
        /**
         * This method should only be used by the repository layer or test to construct the entity without generate any domain event.
         * For business logic purposes use #create(name: String, email: String) method
         */
        fun create(
            id: CustomerId = CustomerId.unique(),
            name: String,
            email: String,
            active: Boolean = false,
            activeUntil: ZonedDateTime? = null,
            subscription: List<Subscription> = listOf(),
        ): Customer =
            Customer(
                id = id,
                name = name,
                email = email,
            ).also {
                it.active = active
                it.activeUntil = activeUntil

                it._subscriptions.addAll(subscription)
            }

        fun create(
            name: String,
            email: String,
        ): Customer {
            val customer = create(id = CustomerId.unique(), name = name, email = email)
            customer.recordEvent(CustomerCreated(customer))
            return customer
        }
    }

    fun subscribe(product: Product): Subscription {
        if (!this.active) {
            throw CustomerNotActiveException(customerId = this.id)
        }
        val subscription = Subscription.to(product = product)
        _subscriptions.add(subscription)
        this.recordEvent(ProductSubscribed(domainId = this.id, productId = product.id))
        return subscription
    }

    @Throws(SubscriptionNotFoundForProductIdException::class)
    fun activeSubscription(productId: ProductId): Subscription {
        val subscription =
            _subscriptions
                .firstOrNull { subscription -> subscription.productId == productId }
                ?.apply { this.activate() }
                ?: throw SubscriptionNotFoundForProductIdException(customerId = id, productId = productId)

        // TODO: send event to subscription activated
        return subscription
    }

    fun getSubscriptionByStatus(status: SubscriptionStatus): List<Subscription> =
        this.subscriptions.filter { subscription -> subscription.status == status }

    fun activate() {
        this.active = true
        this.activeUntil = ZonedDateTime.now().plusDays(30)
        this.recordEvent(CustomerActivated(domainId = this.id))
    }

    fun deactivate() {
        if (!this.active) {
            return
        }
        this.active = false
        this.activeUntil = null
        this.recordEvent(CustomerDeactivated(domainId = this.id))
    }

    fun activationKey(): ActivationKey {
        if (!this.active) {
            throw CustomerNotActiveException(customerId = this.id)
        }
        return ActivationKey.of(customer = this)
    }
}
