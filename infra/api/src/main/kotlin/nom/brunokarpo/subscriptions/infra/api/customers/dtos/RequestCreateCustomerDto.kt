package nom.brunokarpo.subscriptions.infra.api.customers.dtos

data class RequestCreateCustomerDto(
    val name: String,
    val email: String,
)
