package nom.brunokarpo.subscriptions.infra.database.jpa.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import nom.brunokarpo.subscriptions.domain.product.Product
import nom.brunokarpo.subscriptions.domain.product.ProductId
import java.util.UUID

@Entity(name = "products")
class ProductMapper {

	@Id
	lateinit var id: UUID
	lateinit var name: String

	fun toDomain(): Product = Product.create(productId = ProductId.from(id), name = name, emitEvent = false)

	companion object {
		fun from(product: Product): ProductMapper = ProductMapper().also {
			it.id = product.id.value()
			it.name = product.name
		}
	}
}