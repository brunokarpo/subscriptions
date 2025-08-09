package nom.brunokarpo.subscriptions.domain.product

interface ProductRepository {

	fun save(product: Product)

	fun findByName(name: String): Product?
}