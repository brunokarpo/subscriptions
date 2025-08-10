package nom.brunokarpo.subscriptions.application.customer

import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerUniqueEmailException
import nom.brunokarpo.subscriptions.application.usecases.UseCase
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository

class CreateNewCustomerUseCase(
	private val customerRepository: CustomerRepository
): UseCase<CreateNewCustomerUseCase.Input, CreateNewCustomerUseCase.Output> {

	override suspend fun execute(input: Input): Output {
		customerRepository.findByEmail(input.email)?.let {
			throw CustomerUniqueEmailException(input.email)
		}

		val customer = Customer.create(name = input.name, email = input.email)

		customerRepository.save(customer)

		return Output(id = customer.id.toString(), name = customer.name, email = customer.email)
	}

	class Input(val name: String, val email: String)

	class Output(val id: String, val name: String, val email: String)
}