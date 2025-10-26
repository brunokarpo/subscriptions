package nom.brunokarpo.subscriptions.application.customer

import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerByIdNotFoundException
import nom.brunokarpo.subscriptions.application.usecases.UseCase
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.domain.customer.exceptions.SubscriptionNotFoundForProductIdException
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.Subscription
import nom.brunokarpo.subscriptions.domain.product.ProductId

class ActivateSubscriptionUseCase(
    private val customerRepository: CustomerRepository,
) : UseCase<ActivateSubscriptionUseCase.Input, ActivateSubscriptionUseCase.Output> {
    @Throws(SubscriptionNotFoundForProductIdException::class, CustomerByIdNotFoundException::class)
    override suspend fun execute(input: Input): Output {
        val customerId = CustomerId.from(input.customerId)
        val customer = customerRepository.findById(customerId) ?: throw CustomerByIdNotFoundException(customerId)

        val productId = ProductId.from(input.productId)

        val subscription: Subscription = customer.activeSubscription(productId)

        customerRepository.save(customer)

        return Output(
            customerId = customer.id.toString(),
            productId = subscription.productId.toString(),
            status = subscription.status.name,
        )
    }

    class Input(
        val customerId: String,
        val productId: String,
    )

    class Output(
        val customerId: String,
        val productId: String,
        val status: String,
    )
}
