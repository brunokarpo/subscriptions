package nom.brunokarpo.subscriptions.application.customer

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.application.fixtures.CustomerFixture
import nom.brunokarpo.subscriptions.application.fixtures.ProductFixture
import nom.brunokarpo.subscriptions.domain.common.DomainException
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.domain.product.ProductId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ActivateSubscriptionUseCaseTest {

    private lateinit var customerRepository: CustomerRepository

    private lateinit var sut: ActivateSubscriptionUseCase

    @BeforeEach
    fun setUp() {
        customerRepository = mockk(relaxed = true)

        sut = ActivateSubscriptionUseCase(
            customerRepository = customerRepository
        )
    }

    @Test
    fun `should activate product subscription`() = runTest {
        // given
        val customer = CustomerFixture.createCustomer(active = true)
        val product = ProductFixture.createProduct()
        customer.subscribe(product)

        coEvery { customerRepository.findById(customer.id) } returns customer

        // when
        val input = ActivateSubscriptionUseCase.Input(
            customerId = customer.id.toString(),
            productId = product.id.toString()
        )
        val output = sut.execute(input)

        // then
        assertEquals(customer.id.toString(), output.customerId)
        assertEquals(product.id.toString(), output.productId)
        assertEquals("ACTIVE", output.status)

        coVerify { customerRepository.save(customer) }
    }

    @Test
    fun `should throw exception when customer does not have the subscription requested to a product id`() = runTest {
        // given
        val customer = CustomerFixture.createCustomer(active = true)
        val productId = ProductId.unique()

        coEvery { customerRepository.findById(customer.id) } returns customer

        // when
        val input = ActivateSubscriptionUseCase.Input(
            customerId = customer.id.toString(),
            productId = productId.toString()
        )

        val exception = assertThrows<DomainException> {
            sut.execute(input)
        }

        // then
        assertEquals("Customer with id '${customer.id}' does not have a subscription with product id '$productId'", exception.message)
        coVerify(exactly = 0) { customerRepository.save(any()) }
    }
}