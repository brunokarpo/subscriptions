package nom.brunokarpo.subscriptions.infra.database.jpa

import nom.brunokarpo.subscriptions.infra.database.jpa.entities.ProductMapper
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ProductJpaRepository : CrudRepository<ProductMapper, UUID> {
    fun findByNameIgnoreCase(name: String): ProductMapper?
}
