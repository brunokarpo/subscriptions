package nom.brunokarpo.subscriptions.infra.api.controllers

import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.application.product.CreateNewProductUseCase
import nom.brunokarpo.subscriptions.application.product.exceptions.ProductUniqueNameException
import nom.brunokarpo.subscriptions.infra.api.ApiConfigurationTest
import nom.brunokarpo.subscriptions.infra.api.dtos.CreateProductDto
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class ProductsControllerTest: ApiConfigurationTest() {

	@Test
	fun `should create product by name`() = runTest {
		val expectedId = "79e9eb45-2835-49c8-ad3b-c951b591bc7f"
		val expectedProductName = "Product 1"

		coEvery { createProductUseCase.execute(any()) } returns CreateNewProductUseCase.Output(id = expectedId, name = expectedProductName)

		val productDto = CreateProductDto(name = expectedProductName)

		client.post()
			.uri("/v1/products")
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(productDto)
			.exchange()
			.expectStatus().isCreated
			.expectBody()
			.jsonPath("$.name").isEqualTo(expectedProductName)
			.jsonPath("$.id").isEqualTo(expectedId)
	}

	@Test
	fun `should return a handled error when create new product use case throw exception`() = runTest {
		val expectedId = "79e9eb45-2835-49c8-ad3b-c951b591bc7f"
		val expectedProductName = "Product 1"

		coEvery { createProductUseCase.execute(any()) } throws ProductUniqueNameException(expectedProductName)

		val productDto = CreateProductDto(name = expectedProductName)

		client.post()
			.uri("/v1/products")
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(productDto)
			.exchange()
			.expectStatus().isBadRequest
			.expectBody()
			.jsonPath("$.message").isEqualTo("Product with name '$expectedProductName' already exists!")
	}
}