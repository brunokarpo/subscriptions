package nom.brunokarpo.subscriptions.infra.api.customers.dtos

data class ResponseProductSubscriptionCustomerDto(
    val email: String,
    val productName: String,
    val subscriptionStatus: String,
)
