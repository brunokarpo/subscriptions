package nom.brunokarpo.subscriptions.application.product

import nom.brunokarpo.subscriptions.application.product.exceptions.ProductUniqueNameException
import nom.brunokarpo.subscriptions.application.usecases.UseCase
import nom.brunokarpo.subscriptions.domains.product.Product
import nom.brunokarpo.subscriptions.domains.product.ProductRepository
import kotlin.jvm.Throws

class CreateNewProductUseCase(
	private val productRepository: ProductRepository
) : UseCase<CreateNewProductUseCase.Input, CreateNewProductUseCase.Output> {

	@Throws(ProductUniqueNameException::class)
	override fun execute(input: Input): Output {
		productRepository.findByName(input.name)?.let {
			throw ProductUniqueNameException(input.name)
		}

		val product = Product.create(input.name)

		productRepository.save(product)

		return Output(product)
	}

	data class Input(val name: String)

	data class Output(val id: String, val name: String) {
		constructor(product: Product): this(id = product.id.toString(), name = product.name)
	}
}