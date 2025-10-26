package nom.brunokarpo.subscriptions.application.customer

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.application.fixtures.CustomerFixture
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DeactivateCustomerUseCaseTest {

    private lateinit var repository: CustomerRepository

    private lateinit var sut: DeactivateCustomerUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        sut = DeactivateCustomerUseCase(customerRepository = repository)
    }

    @Test
    fun `should deactivate customer`() = runTest {
        // given
        val customer = CustomerFixture.createCustomer(active = true)

        coEvery { repository.findById(customer.id) } returns customer

        // when
        val input = DeactivateCustomerUseCase.Input(customerId = customer.id.toString())
        val output = sut.execute(input)

        // then
        assertEquals(customer.id.toString(), output.customerId)
        assertEquals(customer.name, output.name)
        assertEquals(customer.email, output.email)
        assertFalse(output.active)

        assertFalse { customer.active }

        coVerify(exactly = 1) { repository.save(customer) }
    }
}