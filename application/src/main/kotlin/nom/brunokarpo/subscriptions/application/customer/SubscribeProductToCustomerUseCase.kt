package nom.brunokarpo.subscriptions.application.customer

import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerNotExistsException
import nom.brunokarpo.subscriptions.application.customer.exceptions.ProductNotExistsException
import nom.brunokarpo.subscriptions.application.usecases.UseCase
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.domain.product.ProductRepository
import java.time.format.DateTimeFormatter

class SubscribeProductToCustomerUseCase(
	private val customerRepository: CustomerRepository,
	private val productRepository: ProductRepository
) : UseCase<SubscribeProductToCustomerUseCase.Input, SubscribeProductToCustomerUseCase.Output> {

	override suspend fun execute(input: Input): Output {
		val customer = customerRepository.findByEmail(input.customerEmail) ?: throw CustomerNotExistsException(input.customerEmail)
		val product = productRepository.findByName(input.productName) ?: throw ProductNotExistsException(input.productName)

		customer.subscribe(product)

		customerRepository.save(customer)

		val activationKey = customer.activationKey()

		return Output(
			email = activationKey.email,
			products = activationKey.products.toList(),
			validUntil = activationKey.validUntil.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
		)
	}

	class Input(val customerEmail: String, val productName: String)

	class Output(val email: String, val products: List<String>, val validUntil: String)

}