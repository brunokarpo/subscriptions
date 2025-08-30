package nom.brunokarpo.subscriptions.infra.api.customers

import io.mockk.coEvery
import nom.brunokarpo.subscriptions.application.customer.CreateNewCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.SubscribeProductToCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerByIdNotFoundException
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerUniqueEmailException
import nom.brunokarpo.subscriptions.application.customer.exceptions.ProductNotExistsException
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.infra.api.ApiConfigurationTest
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.CreateCustomerDto
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.ProductSubscriptionDto
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class CustomersControllerTest : ApiConfigurationTest() {

	@Test
	fun `should create customer with name and email`() {
		val expectedId = "c129a079-3bdb-46e7-b578-4a96add93664"
		val expectedName = "Sara Evan"
		val expectedEmail = "sara@email.com"

		coEvery { createNewCustomerUseCase.execute(any()) } returns CreateNewCustomerUseCase.Output(
			id = expectedId,
			name = expectedName,
			email = expectedEmail
		)

		val clientDto = CreateCustomerDto(name = expectedName, email = expectedEmail)

		client.post()
			.uri("/v1/customers")
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(clientDto)
			.exchange()
			.expectStatus().isCreated
			.expectBody()
			.jsonPath("$.name").isEqualTo(expectedName)
			.jsonPath("$.email").isEqualTo(expectedEmail)
			.jsonPath("$.id").isEqualTo(expectedId)
	}

	@Test
	fun `should return bad request when email already exists`() {
		val expectedEmail = "email@email.com"

		coEvery { createNewCustomerUseCase.execute(any()) } throws CustomerUniqueEmailException(expectedEmail)

		val customerDto = CreateCustomerDto(name = "Name", email = expectedEmail)

		client.post()
			.uri("/v1/customers")
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(customerDto)
			.exchange()
			.expectStatus().isBadRequest
			.expectBody()
			.jsonPath("$.message").isEqualTo("Customer with email '$expectedEmail' already exists!")
	}

	@Test
	fun `should subscribe a product to user by name`() {
		val customerId = "c129a079-3bdb-46e7-b578-4a96add93664"
		val productName = "PRODUCT_ID_1"

		val expectedEmail = "<EMAIL>"
		val productName2 = "PRODUCT_ID_2"

		coEvery { subscribeProductToCustomerUseCase.execute(any()) } returns SubscribeProductToCustomerUseCase.Output(
			email = expectedEmail,
			products = listOf(productName, productName2),
			validUntil = "2021-08-31"
		)

		val productSubscriptionDto = ProductSubscriptionDto(productName = productName)

		client.post()
			.uri("/v1/customers/$customerId/products")
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(productSubscriptionDto)
			.exchange()
			.expectStatus().isCreated
			.expectBody()
			.jsonPath("$.email").isEqualTo(expectedEmail)
			.jsonPath("$.products[0]").isEqualTo(productName)
			.jsonPath("$.products[1]").isEqualTo(productName2)
			.jsonPath("$.validUntil").isEqualTo("2021-08-31")
	}

	@Test
	fun `should return bad request if the product does not exist for subscription`() {
		val customerId = "c129a079-3bdb-46e7-b578-4a96add93664"
		val productName = "PRODUCT_ID_1"

		coEvery { subscribeProductToCustomerUseCase.execute(any()) } throws ProductNotExistsException(productName)

		val productSubscriptionDto = ProductSubscriptionDto(productName = productName)

		client.post()
			.uri("/v1/customers/$customerId/products")
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(productSubscriptionDto)
			.exchange()
			.expectStatus().isBadRequest
			.expectBody()
			.jsonPath("$.message").isEqualTo("Product with name '$productName' does not exists!")
	}

	@Test
	fun `should return bad request if the customer does not exists for subscription`() {
		val customerId = "c129a079-3bdb-46e7-b578-4a96add93664"
		val productName = "PRODUCT_ID_1"

		coEvery { subscribeProductToCustomerUseCase.execute(any()) } throws CustomerByIdNotFoundException(
			CustomerId.from(customerId)
		)

		val productSubscriptionDto = ProductSubscriptionDto(productName = productName)

		client.post()
			.uri("/v1/customers/$customerId/products")
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(productSubscriptionDto)
			.exchange()
			.expectStatus().isBadRequest
			.expectBody()
			.jsonPath("$.message").isEqualTo("Customer with id '$customerId' does not exists!")
	}
}