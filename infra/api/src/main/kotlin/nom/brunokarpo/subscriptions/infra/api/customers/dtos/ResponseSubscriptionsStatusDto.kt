package nom.brunokarpo.subscriptions.infra.api.customers.dtos

data class ResponseSubscriptionsStatusDto(
    val customerId: String,
    val subscriptions: List<SubscriptionStatusDto>,
) {
    data class SubscriptionStatusDto(
        val productId: String,
        val status: String,
    )
}
