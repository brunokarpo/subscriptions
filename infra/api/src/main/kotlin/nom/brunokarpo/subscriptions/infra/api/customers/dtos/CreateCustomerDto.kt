package nom.brunokarpo.subscriptions.infra.api.customers.dtos

data class CreateCustomerDto(
	val name: String,
	val email: String
)