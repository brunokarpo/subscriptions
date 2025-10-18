package nom.brunokarpo.subscriptions.application.customer

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RetrieveCustomersSubscriptionByStatusUseCaseTest {
    private lateinit var repository: CustomerRepository

    private lateinit var sut: RetrieveCustomersSubscriptionsRequestedUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)

        sut = RetrieveCustomersSubscriptionsRequestedUseCase(customerRepository = repository)
    }

    @Test
    fun `should retrieve customer's subscriptions by status`(): Unit =
        runTest {
            // given
            val customerId = CustomerId.unique()
            val customer = Customer.create(id = customerId, name = "Test", email = "<EMAIL>")
            customer.activate()

            val product1Id = ProductId.unique()
            val product1Name = "Product 1"
            val product1 = Product.create(productId = product1Id, name = product1Name)
            customer.subscribe(product1)

            val product2Id = ProductId.unique()
            val product2Name = "Product 2"
            val product2 = Product.create(productId = product2Id, name = product2Name)
            customer.subscribe(product2)

            coEvery { repository.findById(customerId) } returns customer

            // when
            val output =
                sut.execute(
                    RetrieveCustomersSubscriptionsRequestedUseCase.Input(customerId = customerId.toString(), status = "REQUESTED"),
                )

            // then
            assertEquals(2, output.subscriptions.size)
            assertTrue(output.subscriptions.any { it.productId == product1Id.toString() && it.status == "REQUESTED" })
            assertTrue(output.subscriptions.any { it.productId == product2Id.toString() && it.status == "REQUESTED" })
        }
}
