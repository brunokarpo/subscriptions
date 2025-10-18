package nom.brunokarpo.subscriptions.domain.product

import nom.brunokarpo.subscriptions.domain.product.events.ProductCreated
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ProductTest {
    @Test
    fun `should generate a ProductCreated event when product is created by name`() {
        // given
        val name = "Product 1"

        // when
        val product = Product.create(name = name)

        // then
        assertNotNull(product.id)
        assertEquals(name, product.name)

        val event = product.domainEvents().first { ev -> ev is ProductCreated } as ProductCreated

        assertEquals(product.id, event.domainId)
        assertNotNull(event.occurredOn)
        assertEquals(name, event.name)
    }

    @Test
    fun `should create a product without generate creation event`() {
        // given
        val productId = ProductId.unique()
        val name = "Product 1"

        // when
        val product: Product = Product.create(productId = productId, name = name, emitEvent = false)

        // then
        assertEquals(productId, product.id)
        assertEquals(name, product.name)
        assertEquals(0, product.domainEvents().size)
    }
}
