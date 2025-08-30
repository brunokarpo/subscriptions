package nom.brunokarpo.subscriptions.application.customer

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerByIdNotFoundException
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CustomerActivateUseCaseTest {

	private lateinit var customerRepository: CustomerRepository

	private lateinit var sut: CustomerActivateUseCase

	@BeforeEach
	fun setUp() {
		customerRepository = mockk(relaxed = true)

		sut = CustomerActivateUseCase(customerRepository = customerRepository)
	}

	@Test
	fun `should activate customer with customer id`() = runTest {
		// given
		val customerId = CustomerId.unique()

		val customer = Customer.create(id = customerId, name = "Customer Name", email = "<EMAIL>")
		assertFalse(customer.active)

		coEvery { customerRepository.findById(customerId) } returns customer

		// when
		val input = CustomerActivateUseCase.Input(customerId = customerId.toString())

		val output = sut.execute(input)

		// then
		assertNotNull(output)
		assertEquals(customerId.toString(), output.customerId)
		assertEquals(customer.activeUntil!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), output.activeUntil)

		val slot = slot<Customer>()
		coVerify(exactly = 1) {
			customerRepository.save(capture(slot))
		}

		val savedCustomer = slot.captured
		assertTrue(savedCustomer.active)
	}

	@Test
	fun `should not activate customer that does not exists`() = runTest {
		// given a customer that does not exist on repository
		val customerId = CustomerId.unique()
		coEvery { customerRepository.findById(customerId) } returns null

		// when call the use case to activate the customer
		val input = CustomerActivateUseCase.Input(customerId = customerId.toString())
		val exception = assertThrows<CustomerByIdNotFoundException> { sut.execute(input) }

		// then validate if the exception was thrown and the message informs that customer by id does not exist
		assertNotNull(exception)
		assertEquals("Customer with id '$customerId' does not exists!", exception.message)
		coVerify(exactly = 0) { customerRepository.save(any()) }
	}
}