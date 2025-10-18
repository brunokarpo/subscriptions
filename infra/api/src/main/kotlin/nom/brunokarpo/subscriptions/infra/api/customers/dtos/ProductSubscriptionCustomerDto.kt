package nom.brunokarpo.subscriptions.infra.api.customers.dtos

data class ProductSubscriptionCustomerDto(
    val email: String,
    val productName: String,
    val subscriptionStatus: String,
)
