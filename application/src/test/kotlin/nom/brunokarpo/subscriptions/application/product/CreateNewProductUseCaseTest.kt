package nom.brunokarpo.subscriptions.application.product

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.application.product.exceptions.ProductUniqueNameException
import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateNewProductUseCaseTest {

	private lateinit var productRepository: ProductRepository

	private lateinit var sut: CreateNewProductUseCase

	@BeforeEach
	fun setUp() {
		productRepository = mockk(relaxed = true) {
			coEvery { findByName(any()) } returns null
		}

		sut = CreateNewProductUseCase(productRepository = productRepository)
	}

	@Test
	fun `should create and save new product`() = runTest {
		// given
		val productName = "Product 1"
		val input = CreateNewProductUseCase.Input(productName)

		// when
		val output = sut.execute(input)

		// then
		assertNotNull(output.id, "Output id should not be null")
		assertEquals(productName, output.name)

		val slot = slot<Product>()
		coVerify {
			productRepository.save(capture(slot))
		}

		val product = slot.captured
		assertEquals(output.id, product.id.toString())
		assertEquals(output.name, product.name)
	}

	@Test
	fun `should throw ProductUniqueNameException when trying to save a product that already exists on repository`() = runTest {
		// given
		val productName = "Product 1"
		val input = CreateNewProductUseCase.Input(productName)

		coEvery {
			productRepository.findByName(productName)
		} returns Product.create(name = productName)

		// when
		val exception = assertThrows<ProductUniqueNameException> {
			 sut.execute(input)
		}

		// then
		assertEquals("Product with name '$productName' already exists!", exception.message)
		coVerify (exactly = 0) {
			productRepository.save(any())
		}
	}
}