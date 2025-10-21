package nom.brunokarpo.subscriptions.infra.database.jpa.entities

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import java.time.ZonedDateTime
import java.util.UUID

@Entity(name = "customers")
class CustomerMapper {
    @Id
    lateinit var id: UUID
    lateinit var name: String
    lateinit var email: String
    var active: Boolean? = null
    var activeUntil: ZonedDateTime? = null

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer")
    lateinit var subscriptions: MutableSet<SubscriptionMapper>

    companion object {
        fun from(customer: Customer): CustomerMapper =
            CustomerMapper().also {
                it.id = customer.id.value()
                it.name = customer.name
                it.email = customer.email
                it.active = customer.active
                it.activeUntil = customer.activeUntil
                it.subscriptions = customer.subscriptions.map { subscription ->
                    SubscriptionMapper.from(
                        customer = it,
                        subscription = subscription
                    )
                }.toMutableSet()
            }
    }

    fun toDomain(): Customer =
        Customer.create(
            id = CustomerId.from(id),
            name = name,
            email = email,
            active = active ?: false,
            activeUntil = activeUntil,
            subscription = subscriptions.map { it.toDomain() }.toList()
        )
}
