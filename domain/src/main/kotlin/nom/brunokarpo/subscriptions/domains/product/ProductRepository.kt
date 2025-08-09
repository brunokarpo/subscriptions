package nom.brunokarpo.subscriptions.domains.product

interface ProductRepository {

	fun save(product: Product)

	fun findByName(name: String): Product?
}