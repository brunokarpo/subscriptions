package nom.brunokarpo.subscriptions.application.customer

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerByIdNotFoundException
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerNotExistsException
import nom.brunokarpo.subscriptions.application.customer.exceptions.ProductNotExistsException
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.domain.customer.exceptions.CustomerNotActiveException
import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId
import nom.brunokarpo.subscriptions.domain.product.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains

class SubscribeProductToCustomerUseCaseTest {
    private lateinit var customerRepository: CustomerRepository
    private lateinit var productRepository: ProductRepository

    private lateinit var sut: SubscribeProductToCustomerUseCase

    @BeforeEach
    fun setUp() {
        customerRepository = mockk(relaxed = true)
        productRepository = mockk(relaxed = true)

        sut =
            SubscribeProductToCustomerUseCase(
                customerRepository = customerRepository,
                productRepository = productRepository,
            )
    }

    @Test
    fun `should subscribe product to customer`() =
        runTest {
            // given
            val customerEmail = "<EMAIL>"
            val customerId = CustomerId.unique()
            val customer = Customer.create(id = customerId, name = "Customer Name", email = customerEmail)
            customer.activate()
            coEvery { customerRepository.findById(customerId) } returns customer

            val productName = "Product Name"
            val productId = ProductId.unique()
            val product = Product.create(productId = productId, name = productName)
            coEvery { productRepository.findByName(productName) } returns product

            val expectedSubscriptionStatus = "REQUESTED"

            // when
            val input =
                SubscribeProductToCustomerUseCase.Input(
                    customerId = customerId.toString(),
                    productName = productName,
                )
            val output = sut.execute(input)

            // then
            assertEquals(customerEmail, output.email)
            assertEquals(productName, output.productName)
            assertEquals(expectedSubscriptionStatus, output.subscriptionStatus)

            coVerify(exactly = 1) { customerRepository.save(customer) }
        }

    @Test
    fun `should not subscribe product to a non existent customer`() =
        runTest {
            // given
            val customerId = CustomerId.unique()
            coEvery { customerRepository.findById(customerId) } returns null

            val productName = "Product Name"
            coEvery { productRepository.findByName(productName) } returns Product.create(name = productName)

            // when
            val input =
                SubscribeProductToCustomerUseCase.Input(
                    customerId = customerId.toString(),
                    productName = productName,
                )
            val exception =
                assertThrows<CustomerByIdNotFoundException> {
                    sut.execute(input)
                }

            // then
            assertNotNull(exception)
            assertEquals("Customer with id '$customerId' does not exists!", exception.message)

            coVerify(exactly = 0) { customerRepository.save(any()) }
        }

    @Test
    fun `should not subscribe non existent product to a customer`() =
        runTest {
            // given
            val customerEmail = "<EMAIL>"
            val customerId = CustomerId.unique()

            val customer = Customer.create(id = customerId, name = "Customer Name", email = customerEmail)
            customer.activate()
            coEvery { customerRepository.findById(customerId) } returns customer
            coEvery { customerRepository.findById(customerId) } returns customer

            val productName = "productName"
            coEvery { productRepository.findByName(productName) } returns null

            // when
            val input =
                SubscribeProductToCustomerUseCase.Input(
                    customerId = customerId.toString(),
                    productName = productName,
                )
            val exception =
                assertThrows<ProductNotExistsException> {
                    sut.execute(input)
                }

            // then
            assertNotNull(exception)
            assertEquals("Product with name '$productName' does not exists!", exception.message)
        }

    @Test
    fun `should not subscribe product to a disabled customer`() =
        runTest {
            // given
            val customerEmail = "<EMAIL>"
            val customerId = CustomerId.unique()
            val customer = Customer.create(id = customerId, name = "Customer Name", email = customerEmail)
            coEvery { customerRepository.findById(customerId) } returns customer

            val productName = "Product Name"
            val productId = ProductId.unique()
            val product = Product.create(productId = productId, name = productName)
            coEvery { productRepository.findByName(productName) } returns product

            // when
            val input =
                SubscribeProductToCustomerUseCase.Input(
                    customerId = customerId.toString(),
                    productName = productName,
                )
            val exception =
                assertThrows<CustomerNotActiveException> {
                    sut.execute(input)
                }

            // then
            assertNotNull(exception)
            assertEquals("Customer with id: $customerId is not active!", exception.message)
        }
}
