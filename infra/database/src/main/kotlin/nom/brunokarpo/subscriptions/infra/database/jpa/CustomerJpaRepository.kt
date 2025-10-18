package nom.brunokarpo.subscriptions.infra.database.jpa

import nom.brunokarpo.subscriptions.infra.database.jpa.entities.CustomerMapper
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface CustomerJpaRepository : CrudRepository<CustomerMapper, UUID> {
    fun findByEmailIgnoreCase(email: String): CustomerMapper?
}
