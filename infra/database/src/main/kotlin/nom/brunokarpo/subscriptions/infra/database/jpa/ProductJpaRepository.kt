package nom.brunokarpo.subscriptions.infra.database.jpa

import nom.brunokarpo.subscriptions.infra.database.jpa.entities.ProductEntity
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ProductJpaRepository : CrudRepository<ProductEntity, UUID> {

	fun findByNameIgnoreCase(name: String): ProductEntity?
}