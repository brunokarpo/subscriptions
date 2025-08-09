package nom.brunokarpo.subscriptions.infra.database.repositories

import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductRepository
import nom.brunokarpo.subscriptions.infra.database.DatabaseConfigurationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class ProductRepositoryTest : DatabaseConfigurationTest() {

	@Autowired
	private lateinit var sut: ProductRepository

	@Test
	fun `should save new created product`() {
		// given
		val name = "Product 1"

		val product = Product.create(name)

		// when
		sut.save(product)

		// then
		val result = sut.findByName(name)
		assertEquals(product.id, result?.id)
		assertEquals(name, result?.name)
	}

	@Test
	@Sql(scripts = ["/create_products.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	fun `should find product by name`() {
		val name = "database product"

		val product = sut.findByName(name)

		assertNotNull(product)
		assertEquals(name, product.name)
		assertEquals("79e9eb45-2835-49c8-ad3b-c951b591bc7f", product.id.value().toString())
	}

	@Test
	fun `should return null when product name does not exists`() {
		val name = "unknown product"

		val product = sut.findByName(name)

		assertNull(product)
	}

	@Test
	@Sql(scripts = ["/create_products.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	fun `should find product by name even when case does not match exactly`() {
		val name = "DATABASE PRODUCT"

		val product = sut.findByName(name)

		assertNotNull(product)
		assertEquals("database product", product.name)
		assertEquals("79e9eb45-2835-49c8-ad3b-c951b591bc7f", product.id.value().toString())
	}
}