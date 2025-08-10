package nom.brunokarpo.subscriptions.infra.api.dtos

data class CreateCustomerDto(
	val name: String,
	val email: String
)