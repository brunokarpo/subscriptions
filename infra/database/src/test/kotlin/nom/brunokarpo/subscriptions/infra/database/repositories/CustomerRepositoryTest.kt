package nom.brunokarpo.subscriptions.infra.database.repositories

import kotlinx.coroutines.test.runTest
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.SubscriptionStatus
import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId
import nom.brunokarpo.subscriptions.infra.database.DatabaseConfigurationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZonedDateTime
import java.util.UUID

class CustomerRepositoryTest : DatabaseConfigurationTest() {
    @Autowired
    private lateinit var sut: CustomerRepository

    @Test
    fun `should save new customer`() =
        runTest {
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
    fun `should find customer by email`() =
        runTest {
            loadDatabase("/create_customers.sql")

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
    fun `should return null when customer email does not exists`() =
        runTest {
            val email = "<EMAIL>"

            val customer = sut.findByEmail(email)

            assertNull(customer)
        }

    @Test
    fun `should find customer by id`() =
        runTest {
            loadDatabase("/create_customers.sql")

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
    fun `should return null when customer not exists by id`() =
        runTest {
            val expectedId = CustomerId.from("d0fe82e3-766f-4c95-ad91-7ee7fd450993")

            val customer = sut.findById(expectedId)

            assertNull(customer)
        }

    @Test
    fun `should update active and active until of existent customer`() =
        runTest {
            loadDatabase("/create_customers.sql")

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
    fun `should load the subscriptions from database`() =
        runTest {
            loadDatabase("/create_customers.sql")
            loadDatabase("/create_products.sql")
            loadDatabase("/create_subscriptions.sql")

            // given
            val expectedEmail = "alice@email.com"

            // when
            val customer = sut.findByEmail(expectedEmail)

            // then
            assertNotNull(customer!!)

            assertEquals(3, customer.subscriptions.size)

            assertNotNull(
                customer.subscriptions.firstOrNull { sub ->
                    sub.productName == "database product 2" &&
                        sub.status == SubscriptionStatus.REQUESTED
                },
            )
            assertNotNull(
                customer.subscriptions.firstOrNull { sub ->
                    sub.productName == "database product 3" &&
                        sub.status == SubscriptionStatus.REQUESTED
                },
            )
            assertNotNull(
                customer.subscriptions.firstOrNull { sub ->
                    sub.productName == "database product 4" &&
                        sub.status == SubscriptionStatus.REQUESTED
                },
            )

            // verify if any domain event was generated
            assertTrue(customer.domainEvents().isEmpty())
        }

    @Test
    fun `should save customer with subscriptions`() =
        runTest {
            loadDatabase("/create_products.sql")

            val customerId = CustomerId.unique()
            val name = "name"
            val email = "email"
            val customer = Customer.create(id = customerId, name = name, email = email)
            customer.activate()

            customer.subscribe(Product.create(ProductId.from("79e9eb45-2835-49c8-ad3b-c951b591bc7f"), ""))
            customer.subscribe(Product.create(ProductId.from("79e9eb45-2835-49c8-ad3b-c951b591bc7e"), ""))

            sut.save(customer)

            val result = sut.findById(customerId)
            assertNotNull(result)
            assertEquals(2, result!!.subscriptions.size)
            assertTrue { result.subscriptions.any { sub -> sub.productId.toString() == "79e9eb45-2835-49c8-ad3b-c951b591bc7f" } }
            assertTrue { result.subscriptions.any { sub -> sub.productId.toString() == "79e9eb45-2835-49c8-ad3b-c951b591bc7e" } }
        }

    @Test
    fun `should update subscriptions when there was saved previously`() =
        runTest {
            loadDatabase("/create_products.sql")

            val customerId = CustomerId.unique()
            val name = "name"
            val email = "email"
            val customer = Customer.create(id = customerId, name = name, email = email)
            customer.activate()

            customer.subscribe(Product.create(ProductId.from("79e9eb45-2835-49c8-ad3b-c951b591bc7f"), ""))
            sut.save(customer)

            customer.subscribe(Product.create(ProductId.from("79e9eb45-2835-49c8-ad3b-c951b591bc7d"), ""))
            sut.save(customer)

            val result = sut.findById(customerId)
            assertNotNull(result)
            assertEquals(2, result!!.subscriptions.size)
            assertTrue { result.subscriptions.any { sub -> sub.productId.toString() == "79e9eb45-2835-49c8-ad3b-c951b591bc7f" } }
            assertTrue { result.subscriptions.any { sub -> sub.productId.toString() == "79e9eb45-2835-49c8-ad3b-c951b591bc7d" } }
        }
}
