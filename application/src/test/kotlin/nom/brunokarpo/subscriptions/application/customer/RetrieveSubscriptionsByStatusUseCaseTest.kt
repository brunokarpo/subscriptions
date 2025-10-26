package nom.brunokarpo.subscriptions.application.customer

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerByIdNotFoundException
import nom.brunokarpo.subscriptions.domain.common.DomainException
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNotNull

class RetrieveSubscriptionsByStatusUseCaseTest {
    private lateinit var repository: CustomerRepository

    private lateinit var sut: RetrieveSubscriptionsByStatusUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)

        sut = RetrieveSubscriptionsByStatusUseCase(customerRepository = repository)
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
            val product1 = Product.create(id = product1Id, name = product1Name)
            customer.subscribe(product1)

            val product2Id = ProductId.unique()
            val product2Name = "Product 2"
            val product2 = Product.create(id = product2Id, name = product2Name)
            customer.subscribe(product2)

            coEvery { repository.findById(customerId) } returns customer

            // when
            val output =
                sut.execute(
                    RetrieveSubscriptionsByStatusUseCase.Input(
                        customerId = customerId.toString(),
                        status = "requested",
                    ),
                )

            // then
            assertEquals(customerId.toString(), output.customerId)
            assertEquals(customer.name, output.customerName)
            assertEquals(customer.email, output.customerEmail)
            assertEquals(2, output.subscriptions.size)
            assertTrue(output.subscriptions.any { it.productId == product1Id.toString() && it.status == "REQUESTED" })
            assertTrue(output.subscriptions.any { it.productId == product2Id.toString() && it.status == "REQUESTED" })
        }

    @Test
    fun `should throw exception when status is unknown`() =
        runTest {
            // given
            val customerId = CustomerId.unique()
            val customer = Customer.create(id = customerId, name = "Test", email = "<EMAIL>")

            coEvery { repository.findById(customerId) } returns customer

            // when
            val exception =
                assertThrows<DomainException> {
                    sut.execute(
                        RetrieveSubscriptionsByStatusUseCase.Input(
                            customerId = customerId.toString(),
                            status = "NEVER_EXIST_SUCH_STATUS",
                        ),
                    )
                }

            assertNotNull(exception)
            assertEquals("Subscription Status unknown: NEVER_EXIST_SUCH_STATUS", exception.message)
        }

    @Test
    fun `should throw customer not found by id exception when customer does not exists`() =
        runTest {
            val customerId = CustomerId.unique()

            coEvery { repository.findById(customerId) } returns null

            val exception =
                assertThrows<CustomerByIdNotFoundException> {
                    sut.execute(
                        RetrieveSubscriptionsByStatusUseCase.Input(
                            customerId = customerId.toString(),
                            status = "requested",
                        ),
                    )
                }

            assertEquals("Customer with id '$customerId' does not exists!", exception.message)
        }
}
