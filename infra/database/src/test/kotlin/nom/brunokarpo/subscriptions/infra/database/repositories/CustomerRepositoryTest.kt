package nom.brunokarpo.subscriptions.infra.database.repositories

import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.infra.database.DatabaseConfigurationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.time.ZonedDateTime
import java.util.UUID

class CustomerRepositoryTest : DatabaseConfigurationTest() {

	@Autowired
	private lateinit var sut: CustomerRepository

	@Test
	fun `should save new customer`() = runTest {
		// given
		val customerId = CustomerId.unique()
		val expectedName = "<NAME>"
		val expectedEmail = "<EMAIL>"

		// when
		val customer = Customer.create(id = customerId, name = expectedName, email = expectedEmail)
		sut.save(customer)

		// then
		val result = sut.findByEmail(expectedEmail)
		assertNotNull(result)
		assertEquals(customerId, result?.id)
		assertEquals(expectedName, result?.name)
		assertEquals(expectedEmail, result?.email)
	}

	@Test
	@Sql(scripts = ["/create_customers.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	fun `should find customer by email`() = runTest {
		val expectedId = UUID.fromString("d0fe82e3-766f-4c95-ad91-7ee7fd450993")
		val expectedName = "John Doe"
		val expectedEmail = "john@email.com"
		val expectedActive = true
		val expectedEndAt = ZonedDateTime.parse("2021-08-31T23:59:59Z")

		val customer = sut.findByEmail(expectedEmail)

		assertNotNull(customer)
		assertEquals(expectedId, customer?.id?.value())
		assertEquals(expectedName, customer?.name)
		assertEquals(expectedEmail, customer?.email)
		assertEquals(expectedActive, customer?.active)
		assertEquals(expectedEndAt, customer?.activeUntil)
	}

	@Test
	fun `should return null when customer email does not exists`() = runTest {
		val email = "<EMAIL>"

		val customer = sut.findByEmail(email)

		assertEquals(customer, null)
	}
}