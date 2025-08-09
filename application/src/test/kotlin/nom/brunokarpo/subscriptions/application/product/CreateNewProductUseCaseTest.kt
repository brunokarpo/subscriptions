package nom.brunokarpo.subscriptions.application.product

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import nom.brunokarpo.subscriptions.application.product.exceptions.ProductUniqueNameException
import nom.brunokarpo.subscriptions.domains.product.Product
import nom.brunokarpo.subscriptions.domains.product.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class CreateNewProductUseCaseTest {

	private lateinit var productRepository: ProductRepository

	private lateinit var sut: CreateNewProductUseCase

	@BeforeEach

	fun setUp() {
		productRepository = mockk(relaxed = true) {
			every { findByName(any()) } returns null
		}

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

	@Test
	fun `should throw ProductUniqueNameException when trying to save a product that already exists on repository`() {
		// given
		val productName = "Product 1"
		val input = CreateNewProductUseCase.Input(productName)

		every {
			productRepository.findByName(productName)
		} returns Product.create(UUID.randomUUID(), productName)

		// when
		val exception = assertThrows<ProductUniqueNameException> {
			sut.execute(input)
		}

		// then
		assertEquals("Product with name '$productName' already exists!", exception.message)
		verify(exactly = 0) {
			productRepository.save(any())
		}
	}
}