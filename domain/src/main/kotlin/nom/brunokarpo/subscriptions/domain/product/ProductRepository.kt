package nom.brunokarpo.subscriptions.domain.product

interface ProductRepository {

	suspend fun save(product: Product)

	suspend fun findByName(name: String): Product?
}