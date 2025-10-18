package nom.brunokarpo.subscriptions.domain.common

abstract class AggregateRoot : Entity {
    private val domainEvents: MutableSet<DomainEvent> = mutableSetOf()

    fun domainEvents(): Set<DomainEvent> = domainEvents.toSet()

    protected fun recordEvent(event: DomainEvent) {
        this.domainEvents.add(event)
    }
}
