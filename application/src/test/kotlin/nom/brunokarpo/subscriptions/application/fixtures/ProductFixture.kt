package nom.brunokarpo.subscriptions.application.fixtures

import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId

object ProductFixture {
    const val PRODUCT_NAME = "Product 1"

    fun createProduct(
        productId: ProductId = ProductId.unique(),
        name: String = PRODUCT_NAME,
    ): Product =
        Product.create(
            id = productId,
            name = name,
        )
}
