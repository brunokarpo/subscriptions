package nom.brunokarpo.subscriptions.infra.api.customers.dtos

data class ProductSubscriptionCustomerDto(
    val email: String,
    val products: Set<String>,
    val validUntil: String,
)
