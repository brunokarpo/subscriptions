package nom.brunokarpo.subscriptions.infra.api.customers.dtos

data class ResponseCustomerDto(
    val id: String,
    val name: String? = null,
    val email: String? = null,
    val activeUntil: String? = null,
)
