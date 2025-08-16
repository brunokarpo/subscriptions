package nom.brunokarpo.subscriptions.infra.api.customers

import io.mockk.coEvery
import nom.brunokarpo.subscriptions.application.customer.CreateNewCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerUniqueEmailException
import nom.brunokarpo.subscriptions.infra.api.ApiConfigurationTest
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.CreateCustomerDto
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
}