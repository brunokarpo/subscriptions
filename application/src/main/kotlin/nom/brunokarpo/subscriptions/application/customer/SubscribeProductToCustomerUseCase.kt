package nom.brunokarpo.subscriptions.application.customer

import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerByIdNotFoundException
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerNotExistsException
import nom.brunokarpo.subscriptions.application.customer.exceptions.ProductNotExistsException
import nom.brunokarpo.subscriptions.application.usecases.UseCase
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.domain.product.ProductRepository
import java.time.format.DateTimeFormatter

class SubscribeProductToCustomerUseCase(
	private val customerRepository: CustomerRepository,
	private val productRepository: ProductRepository
) : UseCase<SubscribeProductToCustomerUseCase.Input, SubscribeProductToCustomerUseCase.Output> {

	@Throws(CustomerByIdNotFoundException::class, CustomerNotExistsException::class, ProductNotExistsException::class)
	override suspend fun execute(input: Input): Output {
		val customerId = CustomerId.from(input.customerId)
		val customer = customerRepository.findById(customerId) ?: throw CustomerByIdNotFoundException(customerId)
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

	class Input(val customerId: String, val productName: String)

	class Output(val email: String, val products: List<String>, val validUntil: String)

}