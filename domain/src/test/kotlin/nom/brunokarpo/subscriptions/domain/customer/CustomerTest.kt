package nom.brunokarpo.subscriptions.domain.customer

import nom.brunokarpo.subscriptions.domain.customer.events.CustomerCreated
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CustomerTest {

	@Test
	fun `should create CustomerCreated event when customer is created by name and email`() {
		val name = "Test"
		val email = "<EMAIL>"

		val customer = Customer.create(name, email)

		assertNotNull(customer.id)
		assertEquals(name, customer.name)
		assertEquals(email, customer.email)

		val event = customer.domainEvents().first{ ev -> ev is CustomerCreated } as CustomerCreated

		assertEquals(customer.id, event.domainId)
		assertNotNull(event.occurredOn)
		assertEquals(name, event.name)
		assertEquals(email, event.email)
	}
}