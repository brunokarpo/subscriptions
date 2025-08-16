package nom.brunokarpo.subscriptions.infra.database.repositories

import nom.brunokarpo.subscriptions.domain.common.Identifier
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.infra.database.jpa.CustomerJpaRepository
import nom.brunokarpo.subscriptions.infra.database.jpa.entities.CustomerMapper
import org.springframework.stereotype.Repository

@Repository
class CustomerDatabaseRepository(
	private val customerJpaRepository: CustomerJpaRepository
) : CustomerRepository {
	override suspend fun save(aggregate: Customer) {
		customerJpaRepository.save(CustomerMapper.from(aggregate))
	}

	override suspend fun findById(id: CustomerId): Customer? {
		TODO("Not yet implemented")s
	}

	override suspend fun findByEmail(email: String): Customer? {
		return customerJpaRepository.findByEmailIgnoreCase(email)?.toDomain()
	}
}