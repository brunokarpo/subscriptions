package nom.brunokarpo.subscriptions.infra.database.jpa.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId
import java.util.UUID

@Entity(name = "products")
class ProductEntity {

	@Id
	lateinit var id: UUID
	lateinit var name: String

	fun toDomain(): Product = Product.create(ProductId.from(id), name)

	companion object {
		fun from(product: Product): ProductEntity = ProductEntity().also {
			it.id = product.id.value()
			it.name = product.name
		}
	}
}