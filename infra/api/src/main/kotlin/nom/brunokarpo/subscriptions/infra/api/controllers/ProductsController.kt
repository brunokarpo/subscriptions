package nom.brunokarpo.subscriptions.infra.api.controllers

import nom.brunokarpo.subscriptions.application.product.CreateNewProductUseCase
import nom.brunokarpo.subscriptions.infra.api.dtos.CreateProductDtoRequest
import nom.brunokarpo.subscriptions.infra.api.dtos.ProductDtoResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/products")
class ProductsController(
	private val createProductUseCase: CreateNewProductUseCase
) {

	@PostMapping
	suspend fun createProduct(@RequestBody createProductDtoRequest: CreateProductDtoRequest): ResponseEntity<ProductDtoResponse> {
		val input = CreateNewProductUseCase.Input(
			name = createProductDtoRequest.name
		)

		val output = createProductUseCase.execute(input)

		val productDtoResponse = ProductDtoResponse(
			id = output.id,
			name = output.name
		)

		return ResponseEntity.status(201).body(productDtoResponse)
	}

}