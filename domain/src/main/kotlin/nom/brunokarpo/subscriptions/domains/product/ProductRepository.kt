package nom.brunokarpo.subscriptions.domains.product

interface ProductRepository {

	fun save(product: Product)
}