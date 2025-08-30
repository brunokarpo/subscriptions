package nom.brunokarpo.subscriptions.infra.database.repositories

import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.infra.database.DatabaseConfigurationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
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

		assertNull(customer)
	}

	@Test
	@Sql(scripts = ["/create_customers.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	fun `should find customer by id`() = runTest {
		val expectedId = CustomerId.from("d0fe82e3-766f-4c95-ad91-7ee7fd450993")
		val expectedName = "John Doe"
		val expectedEmail = "john@email.com"
		val expectedActive = true
		val expectedEndAt = ZonedDateTime.parse("2021-08-31T23:59:59Z")

		val customer = sut.findById(expectedId)

		assertNotNull(customer)
		assertEquals(expectedId, customer?.id)
		assertEquals(expectedName, customer?.name)
		assertEquals(expectedEmail, customer?.email)
		assertEquals(expectedActive, customer?.active)
		assertEquals(expectedEndAt, customer?.activeUntil)
	}

	@Test
	fun `should return null when customer not exists by id`() = runTest {
		val expectedId = CustomerId.from("d0fe82e3-766f-4c95-ad91-7ee7fd450993")

		val customer = sut.findById(expectedId)

		assertNull(customer)
	}

	@Test
	@Sql(scripts = ["/create_customers.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	fun `should update active and active untl of existent customer`() = runTest {
		// given
		val expectedEmail = "sara@email.com"
		val customer = sut.findByEmail(expectedEmail)
		assertNotNull(customer)
		assertFalse(customer!!.active)
		assertEquals(ZonedDateTime.parse("2021-08-31T23:59:59Z"), customer.activeUntil)

		// when
		customer.activate()
		sut.save(customer)

		// then
		val result = sut.findByEmail(expectedEmail)
		assertNotNull(result)
		assertEquals(customer.id, result?.id)
		assertEquals(customer.name, result?.name)
		assertEquals(customer.email, result?.email)
		assertTrue(result?.active ?: false)
		assertTrue(result?.activeUntil!!.isAfter(ZonedDateTime.now()))
	}

	@Test
	@Sql(scripts = ["/create_customers.sql", "/create_subscriptions.sql", "/create_products.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	fun `should load the subscriptions from database`() = runTest {
		// given
		val expectedEmail = "alice@email.com"

		// when
		val customer = sut.findByEmail(expectedEmail)

		// then
		assertNotNull(customer!!)

		val activationKey = customer.activationKey()
		assertNotNull(activationKey)
		assertEquals(3, activationKey.products.size)
		assertTrue(activationKey.products.contains("database product 2"))
		assertTrue(activationKey.products.contains("database product 3"))
		assertTrue(activationKey.products.contains("database product 4"))

		// verify if any domain event was generated
		assertTrue(customer.domainEvents().isEmpty())
	}
}