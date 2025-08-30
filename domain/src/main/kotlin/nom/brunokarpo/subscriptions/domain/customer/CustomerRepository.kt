package nom.brunokarpo.subscriptions.domain.customer

import nom.brunokarpo.subscriptions.domain.common.Repository

interface CustomerRepository : Repository<Customer, CustomerId> {

	suspend fun findByEmail(email: String): Customer?
}