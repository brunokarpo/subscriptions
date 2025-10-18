package nom.brunokarpo.subscriptions.domain.product

import nom.brunokarpo.subscriptions.domain.common.Repository

interface ProductRepository : Repository<Product, ProductId> {
    suspend fun findByName(name: String): Product?
}
