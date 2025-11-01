package nom.brunokarpo.subscriptions.application.customer

import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerByIdNotFoundException
import nom.brunokarpo.subscriptions.application.usecases.UseCase
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository

class DeactivateCustomerUseCase(
    private val customerRepository: CustomerRepository,
) : UseCase<DeactivateCustomerUseCase.Input, DeactivateCustomerUseCase.Output> {
    @Throws(CustomerByIdNotFoundException::class)
    override suspend fun execute(input: Input): Output {
        val customerId = CustomerId.from(input.customerId)
        val customer = customerRepository.findById(customerId) ?: throw CustomerByIdNotFoundException(customerId)

        customer.deactivate()

        customerRepository.save(customer)

        return Output(
            customerId = customer.id.toString(),
            name = customer.name,
            email = customer.email,
            active = customer.active,
        )
    }

    class Input(
        val customerId: String,
    )

    class Output(
        val customerId: String,
        val name: String,
        val email: String,
        val active: Boolean,
    )
}
