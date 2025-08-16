package nom.brunokarpo.subscriptions.infra.database.repositories

import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.infra.database.jpa.CustomerJpaRepository
import nom.brunokarpo.subscriptions.infra.database.jpa.entities.CustomerMapper
import org.springframework.stereotype.Repository

@Repository
class CustomerDatabaseRepository(
	private val customerJpaRepository: CustomerJpaRepository
) : CustomerRepository {
	override suspend fun save(customer: Customer) {
		customerJpaRepository.save(CustomerMapper.from(customer))
	}

	override suspend fun findByEmail(email: String): Customer? {
		return customerJpaRepository.findByEmailIgnoreCase(email)?.toDomain()
	}
}