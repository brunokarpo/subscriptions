package nom.brunokarpo.subscriptions.infra.database.jpa.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import java.util.UUID

@Entity(name = "customers")
class CustomerEntity {

	@Id
	lateinit var id: UUID
	lateinit var name: String
	lateinit var email: String

	companion object {
		fun from(customer: Customer): CustomerEntity = CustomerEntity().also {
			it.id = customer.id.value()
			it.name = customer.name
			it.email = customer.email
		}
	}

	fun toDomain(): Customer = Customer.create(
			id = CustomerId.from(id),
			name = name,
			email = email)
}