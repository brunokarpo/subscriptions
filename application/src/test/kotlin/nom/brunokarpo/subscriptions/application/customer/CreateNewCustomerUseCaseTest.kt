package nom.brunokarpo.subscriptions.application.customer

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerUniqueEmailException
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateNewCustomerUseCaseTest {
    private lateinit var customerRepository: CustomerRepository

    private lateinit var sut: CreateNewCustomerUseCase

    @BeforeEach
    fun setUp() {
        customerRepository =
            mockk(relaxed = true) {
                coEvery { findByEmail(any()) } returns null
            }

        sut = CreateNewCustomerUseCase(customerRepository = customerRepository)
    }

    @Test
    fun `should create a new customer with name and email`() =
        runTest {
            // given
            val expectedName = "Name"
            val expectedEmail = "<EMAIL>"

            val input = CreateNewCustomerUseCase.Input(name = expectedName, email = expectedEmail)

            // when
            val output = sut.execute(input)

            // then
            // validate output
            assertNotNull(output.id)
            assertEquals(expectedName, output.name)
            assertEquals(expectedEmail, output.email)

            val slot = slot<Customer>()
            coVerify {
                customerRepository.save(capture(slot))
            }

            val customer = slot.captured
            assertEquals(output.id, customer.id.toString())
            assertEquals(expectedName, customer.name)
            assertEquals(expectedEmail, customer.email)
        }

    @Test
    fun `should not save customer if already exists a customer with same email saved`() =
        runTest {
            // given
            val expectedName = "Name"
            val expectedEmail = "<EMAIL>"

            coEvery { customerRepository.findByEmail(expectedEmail) } returns Customer.create(name = expectedName, email = expectedEmail)

            // when
            val input = CreateNewCustomerUseCase.Input(name = expectedName, email = expectedEmail)
            val exception = assertThrows<CustomerUniqueEmailException> { sut.execute(input) }

            // then
            assertEquals("Customer with email '$expectedEmail' already exists!", exception.message)
            coVerify(exactly = 0) {
                customerRepository.save(any())
            }
        }
}
