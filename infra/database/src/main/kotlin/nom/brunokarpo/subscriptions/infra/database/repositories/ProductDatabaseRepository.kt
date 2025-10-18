package nom.brunokarpo.subscriptions.infra.database.repositories

import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId
import nom.brunokarpo.subscriptions.domain.product.ProductRepository
import nom.brunokarpo.subscriptions.infra.database.jpa.ProductJpaRepository
import nom.brunokarpo.subscriptions.infra.database.jpa.entities.ProductMapper
import org.springframework.stereotype.Repository

@Repository
class ProductDatabaseRepository(
	private val productJpaRepository: ProductJpaRepository
) : ProductRepository {

	override suspend fun save(aggregate: Product) {
		productJpaRepository.save(ProductMapper.from(aggregate))
	}

    override suspend fun findById(id: ProductId): Product? {
        return productJpaRepository.findById(id.value()).orElse(null)?.toDomain()
    }

    override suspend fun findByName(name: String): Product? {
		return productJpaRepository.findByNameIgnoreCase(name)?.toDomain()
	}
}