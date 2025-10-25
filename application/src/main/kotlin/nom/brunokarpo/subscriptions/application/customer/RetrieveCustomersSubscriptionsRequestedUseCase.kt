package nom.brunokarpo.subscriptions.application.customer

import nom.brunokarpo.subscriptions.application.usecases.UseCase
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.Subscription
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.SubscriptionStatus

class RetrieveCustomersSubscriptionsRequestedUseCase(
    private val customerRepository: CustomerRepository,
) : UseCase<RetrieveCustomersSubscriptionsRequestedUseCase.Input, RetrieveCustomersSubscriptionsRequestedUseCase.Output> {
    override suspend fun execute(input: Input): Output {
        val customer = customerRepository.findById(CustomerId.from(input.customerId))!! // TODO: test when customer does not exists

        val subscriptions: List<Subscription> = customer.getSubscriptionByStatus(SubscriptionStatus.of(input.status))

        return Output(
            customerId = customer.id.toString(),
            customerName = customer.name,
            customerEmail = customer.email,
            subscriptions = subscriptions.map { Output.SubscriptionStatus(it.productId.toString(), it.status.name) },
        )
    }

    class Input(
        val customerId: String,
        val status: String,
    )

    class Output(
        val customerId: String,
        val customerName: String,
        val customerEmail: String,
        val subscriptions: List<SubscriptionStatus>,
    ) {
        class SubscriptionStatus(
            val productId: String,
            val status: String,
        )
    }
}
