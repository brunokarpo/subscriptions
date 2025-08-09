package nom.brunokarpo.subscriptions.application.product

import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import nom.brunokarpo.subscriptions.domains.product.Product
import nom.brunokarpo.subscriptions.domains.product.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateNewProductUseCaseTest {

	private lateinit var productRepository: ProductRepository

	private lateinit var sut: CreateNewProductUseCase

	@BeforeEach

	fun setUp() {
		productRepository = mockk(relaxed = true)

		sut = CreateNewProductUseCase(productRepository = productRepository)
	}

	@Test
	fun `should create and save new product`() {
		// given
		val productName = "Product 1"
		val input = CreateNewProductUseCase.Input(productName)

		// when
		val output = sut.execute(input)


		// then
		assertNotNull(output.id, "Output id should not be null")
		assertEquals(productName, output.name)

		val slot = slot<Product>()
		verify {
			productRepository.save(capture(slot))
		}

		val product = slot.captured
		assertEquals(output.id, product.id.toString())
		assertEquals(output.name, product.name)
	}
}