package nom.brunokarpo.subscriptions.infra.database.jpa.entities

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
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

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "subscription",
		joinColumns = [JoinColumn(name = "customer_id")],
		inverseJoinColumns = [JoinColumn(name = "product_id")]
	)
	var products: Set<ProductMapper> = setOf()

	companion object {
		fun from(customer: Customer): CustomerMapper = CustomerMapper().also {
			it.id = customer.id.value()
			it.name = customer.name
			it.email = customer.email
			it.active = customer.active
			it.activeUntil = customer.activeUntil
		}
	}

	fun toDomain(): Customer = Customer.create(
		id = CustomerId.from(id),
		name = name,
		email = email,
		active = active ?: false,
		activeUntil = activeUntil,
		products = products.map { product -> product.toDomain() }
	)
}