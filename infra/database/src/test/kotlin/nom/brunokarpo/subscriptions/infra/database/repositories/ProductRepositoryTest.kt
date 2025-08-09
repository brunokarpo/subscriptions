package nom.brunokarpo.subscriptions.infra.database.repositories

import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductRepository
import nom.brunokarpo.subscriptions.infra.database.DatabaseConfigurationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

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
}