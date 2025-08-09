package nom.brunokarpo.subscriptions.infra.database.repositories

import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductRepository
import nom.brunokarpo.subscriptions.infra.database.jpa.ProductJpaRepository
import nom.brunokarpo.subscriptions.infra.database.jpa.entities.ProductEntity
import org.springframework.stereotype.Repository

@Repository
class ProductDatabaseRepository(
	private val productJpaRepository: ProductJpaRepository
) : ProductRepository {

	override fun save(product: Product) {
		productJpaRepository.save(ProductEntity.from(product))
	}

	override fun findByName(name: String): Product? {
		return productJpaRepository.findByName(name)?.toDomain()
	}
}