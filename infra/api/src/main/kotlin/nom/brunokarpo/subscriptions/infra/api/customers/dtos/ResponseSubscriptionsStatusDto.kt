package nom.brunokarpo.subscriptions.infra.api.customers.dtos

data class ResponseSubscriptionsStatusDto(
    val customerId: String,
    val name: String,
    val email: String,
    val subscriptions: List<SubscriptionStatusDto>,
) {
    data class SubscriptionStatusDto(
        val productId: String,
        val status: String,
    )
}
