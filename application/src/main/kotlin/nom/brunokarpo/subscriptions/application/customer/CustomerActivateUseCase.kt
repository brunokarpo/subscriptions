package nom.brunokarpo.subscriptions.application.customer

import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerByIdNotFoundException
import nom.brunokarpo.subscriptions.application.usecases.UseCase
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import java.time.format.DateTimeFormatter

class CustomerActivateUseCase(
    private val customerRepository: CustomerRepository,
) : UseCase<CustomerActivateUseCase.Input, CustomerActivateUseCase.Output> {
    override suspend fun execute(input: Input): Output {
        val customerId = CustomerId.from(input.customerId)

        val customer = customerRepository.findById(customerId) ?: throw CustomerByIdNotFoundException(customerId)
        customer.activate()

        customerRepository.save(customer)

        return Output(
            customerId = customerId.toString(),
            name = customer.name,
            email = customer.email,
            activeUntil = customer.activeUntil!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        )
    }

    class Input(
        val customerId: String,
    )

    class Output(
        val customerId: String,
        val name: String,
        val email: String,
        val activeUntil: String,
    )
}
