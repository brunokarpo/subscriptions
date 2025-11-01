package nom.brunokarpo.subscriptions.infra.api.customers

import io.mockk.coEvery
import nom.brunokarpo.subscriptions.application.customer.ActivateCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.ActivateSubscriptionUseCase
import nom.brunokarpo.subscriptions.application.customer.CreateNewCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.DeactivateCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.RetrieveSubscriptionsByStatusUseCase
import nom.brunokarpo.subscriptions.application.customer.SubscribeProductToCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerByIdNotFoundException
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerUniqueEmailException
import nom.brunokarpo.subscriptions.application.customer.exceptions.ProductNotExistsException
import nom.brunokarpo.subscriptions.domain.common.DomainException
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.exceptions.SubscriptionNotFoundForProductIdException
import nom.brunokarpo.subscriptions.domain.product.ProductId
import nom.brunokarpo.subscriptions.infra.api.ApiConfigurationTest
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.RequestCreateCustomerDto
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.RequestProductSubscriptionDto
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.util.UUID

class CustomersControllerTest : ApiConfigurationTest() {
    @Test
    fun `should create customer with name and email`() {
        val expectedId = "c129a079-3bdb-46e7-b578-4a96add93664"
        val expectedName = "Sara Evan"
        val expectedEmail = "sara@email.com"

        coEvery { createNewCustomerUseCase.execute(any()) } returns
            CreateNewCustomerUseCase.Output(
                id = expectedId,
                name = expectedName,
                email = expectedEmail,
            )

        val clientDto = RequestCreateCustomerDto(name = expectedName, email = expectedEmail)

        client
            .post()
            .uri("/v1/customers")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(clientDto)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody()
            .jsonPath("$.name")
            .isEqualTo(expectedName)
            .jsonPath("$.email")
            .isEqualTo(expectedEmail)
            .jsonPath("$.id")
            .isEqualTo(expectedId)
    }

    @Test
    fun `should return bad request when email already exists`() {
        val expectedEmail = "email@email.com"

        coEvery { createNewCustomerUseCase.execute(any()) } throws CustomerUniqueEmailException(expectedEmail)

        val customerDto = RequestCreateCustomerDto(name = "Name", email = expectedEmail)

        client
            .post()
            .uri("/v1/customers")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(customerDto)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Customer with email '$expectedEmail' already exists!")
    }

    @Test
    fun `should subscribe a product to user by name`() {
        val customerId = "c129a079-3bdb-46e7-b578-4a96add93664"
        val productName = "PRODUCT_ID_1"

        val expectedEmail = "<EMAIL>"

        coEvery { subscribeProductToCustomerUseCase.execute(any()) } returns
            SubscribeProductToCustomerUseCase.Output(
                email = expectedEmail,
                productName = productName,
                subscriptionStatus = "REQUESTED",
            )

        val requestProductSubscriptionDto = RequestProductSubscriptionDto(productName = productName)

        client
            .post()
            .uri("/v1/customers/$customerId/subscriptions")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestProductSubscriptionDto)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody()
            .jsonPath("$.email")
            .isEqualTo(expectedEmail)
            .jsonPath("$.productName")
            .isEqualTo(productName)
            .jsonPath("$.subscriptionStatus")
            .isEqualTo("REQUESTED")
    }

    @Test
    fun `should return bad request if the product does not exist for subscription`() {
        val customerId = "c129a079-3bdb-46e7-b578-4a96add93664"
        val productName = "PRODUCT_ID_1"

        coEvery { subscribeProductToCustomerUseCase.execute(any()) } throws ProductNotExistsException(productName)

        val requestProductSubscriptionDto = RequestProductSubscriptionDto(productName = productName)

        client
            .post()
            .uri("/v1/customers/$customerId/subscriptions")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestProductSubscriptionDto)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Product with name '$productName' does not exists!")
    }

    @Test
    fun `should return bad request if the customer does not exists for subscription`() {
        val customerId = "c129a079-3bdb-46e7-b578-4a96add93664"
        val productName = "PRODUCT_ID_1"

        coEvery { subscribeProductToCustomerUseCase.execute(any()) } throws
            CustomerByIdNotFoundException(
                CustomerId.from(customerId),
            )

        val requestProductSubscriptionDto = RequestProductSubscriptionDto(productName = productName)

        client
            .post()
            .uri("/v1/customers/$customerId/subscriptions")
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(requestProductSubscriptionDto)
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Customer with id '$customerId' does not exists!")
    }

    @Test
    fun `should list customer subscription by status`() {
        val customerId = "c129a079-3bdb-46e7-b578-4a96add93664"
        val customerName = "Customer Name"
        val customerEmail = "customer@email.com"
        val productId1 = "PRODUCT_ID_1"
        val productId2 = "PRODUCT_ID_2"

        coEvery {
            retrieveCustomersSubscriptionByStatusUseCase.execute(any())
        } returns
            RetrieveSubscriptionsByStatusUseCase.Output(
                customerId = customerId,
                customerName = customerName,
                customerEmail = customerEmail,
                subscriptions =
                    listOf(
                        RetrieveSubscriptionsByStatusUseCase.Output.SubscriptionStatus(
                            productId = productId1,
                            status = "REQUESTED",
                        ),
                        RetrieveSubscriptionsByStatusUseCase.Output.SubscriptionStatus(
                            productId = productId2,
                            status = "REQUESTED",
                        ),
                    ),
            )

        client
            .get()
            .uri("/v1/customers/$customerId/subscriptions?status=REQUESTED")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.customerId")
            .isEqualTo(customerId)
            .jsonPath("$.name")
            .isEqualTo(customerName)
            .jsonPath("$.email")
            .isEqualTo(customerEmail)
            .jsonPath("$.subscriptions[0].productId")
            .isEqualTo(productId1)
            .jsonPath("$.subscriptions[0].status")
            .isEqualTo("REQUESTED")
            .jsonPath("$.subscriptions[1].productId")
            .isEqualTo(productId2)
            .jsonPath("$.subscriptions[1].status")
            .isEqualTo("REQUESTED")
    }

    @Test
    fun `should return bad request error when subscription status is unknown`() {
        // given
        val customerId = "c129a079-3bdb-46e7-b578-4a96add93664"

        // when
        coEvery {
            retrieveCustomersSubscriptionByStatusUseCase.execute(any())
        } throws object : DomainException("Unknown status: REQUESTED") {}

        client
            .get()
            .uri("/v1/customers/$customerId/subscriptions?status=REQUESTED")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Unknown status: REQUESTED")
    }

    @Test
    fun `should return 404 when customer is not found by id`() {
        // given
        val customerId = "c129a079-3bdb-46e7-b578-4a96add93664"

        // when
        coEvery {
            retrieveCustomersSubscriptionByStatusUseCase.execute(any())
        } throws CustomerByIdNotFoundException(customerId = CustomerId.from(customerId))

        client
            .get()
            .uri("/v1/customers/$customerId/subscriptions?status=REQUESTED")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Customer with id '$customerId' does not exists!")
    }

    @Test
    fun `should activate a customer by identifier`() {
        val customerId = "c129a079-3bdb-46e7-b578-4a96add93664"
        val customerName = "Customer Name"
        val customerEmail = "customer@email.com"
        val activeUntil = "2021-08-31T23:59:59"

        coEvery { customerActivateUseCase.execute(any()) } returns
            ActivateCustomerUseCase.Output(
                customerId = customerId,
                name = customerName,
                email = customerEmail,
                activeUntil = activeUntil,
            )

        client
            .patch()
            .uri("/v1/customers/$customerId/activate")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo(customerId)
            .jsonPath("$.activeUntil")
            .isEqualTo(activeUntil)
            .jsonPath("$.name")
            .isEqualTo(customerName)
            .jsonPath("$.email")
            .isEqualTo(customerEmail)
    }

    @Test
    fun `should deactivate a customer by identifier`() {
        val customerId = "c129a079-3bdb-46e7-b578-4a96add93664"
        val customerName = "Customer Name"
        val customerEmail = "customer@email.com"
        val active = false

        coEvery { deactivateCustomerUseCase.execute(any()) } returns
            DeactivateCustomerUseCase.Output(
                customerId = customerId,
                name = customerName,
                email = customerEmail,
                active = active,
            )

        client
            .patch()
            .uri("/v1/customers/$customerId/deactivate")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo(customerId)
            .jsonPath("$.name")
            .isEqualTo(customerName)
            .jsonPath("$.email")
            .isEqualTo(customerEmail)
            .jsonPath("$.active")
            .isEqualTo(active)
    }

    @Test
    fun `should activate subscription`() {
        val customerId = "c129a079-3bdb-46e7-b578-4a96add93664"
        val productId = "c129a079-3bdb-46e7-b578-4a96add93664"

        coEvery {
            activateSubscriptionUseCase.execute(any())
        } returns
            ActivateSubscriptionUseCase.Output(
                customerId = customerId,
                productId = productId,
                status = "ACTIVE",
            )

        client
            .patch()
            .uri("/v1/customers/$customerId/subscriptions/products/$productId/activate")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.customerId")
            .isEqualTo(customerId)
            .jsonPath("$.productId")
            .isEqualTo(productId)
            .jsonPath("$.status")
            .isEqualTo("ACTIVE")
    }

    @Test
    fun `should throw not found status when customer does not find`() {
        val customerId = UUID.randomUUID().toString()
        val productId = UUID.randomUUID().toString()

        coEvery {
            activateSubscriptionUseCase.execute(any())
        } throws CustomerByIdNotFoundException(customerId = CustomerId.from(customerId))

        client
            .patch()
            .uri("/v1/customers/$customerId/subscriptions/products/$productId/activate")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Customer with id '$customerId' does not exists!")
    }

    @Test
    fun `should throw not found status when customer does not have subscription to product`() {
        val customerId = UUID.randomUUID().toString()
        val productId = UUID.randomUUID().toString()

        coEvery {
            activateSubscriptionUseCase.execute(any())
        } throws
            SubscriptionNotFoundForProductIdException(
                customerId = CustomerId.from(customerId),
                productId = ProductId.from(productId),
            )

        client
            .patch()
            .uri("/v1/customers/$customerId/subscriptions/products/$productId/activate")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Customer with id '$customerId' does not have a subscription with product id '$productId'")
    }
}
