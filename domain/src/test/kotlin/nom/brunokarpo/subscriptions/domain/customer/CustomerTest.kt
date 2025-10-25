package nom.brunokarpo.subscriptions.domain.customer

import nom.brunokarpo.subscriptions.domain.customer.events.CustomerActivated
import nom.brunokarpo.subscriptions.domain.customer.events.CustomerCreated
import nom.brunokarpo.subscriptions.domain.customer.events.ProductSubscribed
import nom.brunokarpo.subscriptions.domain.customer.exceptions.CustomerNotActiveException
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.Subscription
import nom.brunokarpo.subscriptions.domain.customer.subscriptions.SubscriptionStatus
import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime
import kotlin.test.assertContains

class CustomerTest {
    @Test
    fun `should create CustomerCreated event when customer is created by name and email`() {
        val name = "Test"
        val email = "<EMAIL>"

        val customer = Customer.create(name, email)

        assertNotNull(customer.id)
        assertEquals(name, customer.name)
        assertEquals(email, customer.email)

        val event = customer.domainEvents().first { ev -> ev is CustomerCreated } as CustomerCreated

        assertEquals(customer.id, event.domainId)
        assertNotNull(event.occurredOn)
        assertEquals(name, event.name)
        assertEquals(email, event.email)
    }

    @Test
    fun `should subscribe a product`() {
        // given
        val customerId = CustomerId.unique()
        val expectedName = "Test"
        val expectedEmail = "<EMAIL>"

        val customer = Customer.create(id = customerId, name = expectedName, email = expectedEmail)
        customer.activate()

        val productId = ProductId.unique()
        val expectedProductName = "Product 1"
        val product = Product.create(id = productId, name = expectedProductName)

        // when
        val subscription = customer.subscribe(product)

        // then
        assertEquals(1, customer.subscriptions.size)
        assertContains(customer.subscriptions, subscription)
        assertEquals(SubscriptionStatus.REQUESTED, subscription.status)

        // validate domain event
        val event = customer.domainEvents().firstOrNull { ev -> ev is ProductSubscribed } as ProductSubscribed
        assertNotNull(event)
        assertEquals(customerId, event.domainId)
        assertNotNull(event.occurredOn)
        assertEquals(productId, event.productId)
    }

    @Test
    fun `should not allow product subscription if customer is disabled`() {
        // given
        val customerId = CustomerId.unique()
        val expectedName = "Test"
        val expectedEmail = "<EMAIL>"

        val customer = Customer.create(id = customerId, name = expectedName, email = expectedEmail)

        val productId = ProductId.unique()
        val expectedProductName = "Product 1"
        val product = Product.create(id = productId, name = expectedProductName)

        // when
        val exception =
            assertThrows<CustomerNotActiveException> {
                customer.subscribe(product)
            }

        // then
        assertEquals("Customer with id: $customerId is not active!", exception.message)
        assertTrue(customer.domainEvents().isEmpty())
        assertThrows<CustomerNotActiveException> { customer.activationKey() }
    }

    @Test
    fun `should not generate activation key if customer is not active`() {
        // given
        val customerId = CustomerId.unique()
        val expectedName = "Test"
        val expectedEmail = "<EMAIL>"

        val customer = Customer.create(id = customerId, name = expectedName, email = expectedEmail)

        // when
        val exception =
            assertThrows<CustomerNotActiveException> {
                customer.activationKey()
            }

        // then
        assertEquals("Customer with id: $customerId is not active!", exception.message)
    }

    @Test
    fun `should create customer and generate the validation of 30 days to activation key`() {
        // given
        val customerId = CustomerId.unique()
        val expectedName = "Test"
        val expectedEmail = "<EMAIL>"

        val customer = Customer.create(id = customerId, name = expectedName, email = expectedEmail)

        // when
        customer.activate()

        // then
        val activationKey = customer.activationKey()

        // then
        val expectedMinimumValidation = ZonedDateTime.now().plusDays(30).minusHours(1)
        assertTrue(activationKey.validUntil.isAfter(expectedMinimumValidation))

        val event = customer.domainEvents().firstOrNull { ev -> ev is CustomerActivated } as CustomerActivated
        assertNotNull(event)
        assertEquals(customerId, event.domainId)
        assertNotNull(event.occurredOn)
    }

    @Test
    fun `should retrieve customer's subscriptions by status`() {
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

        // when
        val result: List<Subscription> = customer.getSubscriptionByStatus(SubscriptionStatus.REQUESTED)

        // then
        assertEquals(2, result.size)
        assertTrue(result.any { it.productId == product1Id })
        assertTrue(result.any { it.productId == product2Id })
    }

    @Test
    fun `should activate subscription`() {
        val customer = Customer.create(name = "Test", email = "")
        customer.activate()

        val product = Product.create(name = "Product 1")
        customer.subscribe(product)

        val subscription = customer.activeSubscription(product.id)
        assertTrue(customer.subscriptions.contains(subscription))

        assertEquals(product.id, subscription.productId)
        assertEquals(SubscriptionStatus.ACTIVE, subscription.status)
    }
}
