package nom.brunokarpo.subscriptions.infra.database.jpa

import nom.brunokarpo.subscriptions.infra.database.jpa.entities.CustomerEntity
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface CustomerJpaRepository : CrudRepository<CustomerEntity, UUID> {
	fun findByEmailIgnoreCase(email: String): CustomerEntity?
}