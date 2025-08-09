package nom.brunokarpo.subscriptions.infra.api.controllers

import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.application.product.CreateNewProductUseCase
import nom.brunokarpo.subscriptions.application.product.exceptions.ProductUniqueNameException
import nom.brunokarpo.subscriptions.infra.api.ApiConfigurationTest
import nom.brunokarpo.subscriptions.infra.api.dtos.CreateProductDtoRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

class ProductsControllerTest: ApiConfigurationTest() {

	@Autowired
	private lateinit var client: WebTestClient

	@Test
	fun `should create product by name`() = runTest {
		val expectedId = "79e9eb45-2835-49c8-ad3b-c951b591bc7f"
		val expectedProductName = "Product 1"

		coEvery { createProductUseCase.execute(any()) } returns CreateNewProductUseCase.Output(id = expectedId, name = expectedProductName)

		val productDto = CreateProductDtoRequest(name = expectedProductName)

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

		val productDto = CreateProductDtoRequest(name = expectedProductName)

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