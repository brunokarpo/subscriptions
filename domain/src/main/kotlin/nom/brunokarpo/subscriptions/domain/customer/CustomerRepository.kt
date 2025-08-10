package nom.brunokarpo.subscriptions.domain.customer

interface CustomerRepository {

	suspend fun save(customer: Customer)

	suspend fun findByEmail(email: String): Customer?
}