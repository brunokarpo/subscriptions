package nom.brunokarpo.subscriptions.domain.common

interface Repository<A : AggregateRoot, ID : Identifier<*>> {

	suspend fun save(aggregate: A)
	suspend fun findById(id: ID): A?
}