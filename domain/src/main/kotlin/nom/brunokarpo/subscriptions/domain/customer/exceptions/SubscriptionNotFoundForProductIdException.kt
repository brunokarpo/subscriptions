package nom.brunokarpo.subscriptions.domain.customer.exceptions

import nom.brunokarpo.subscriptions.domain.common.DomainException
import nom.brunokarpo.subscriptions.domain.customer.CustomerId
import nom.brunokarpo.subscriptions.domain.product.ProductId

class SubscriptionNotFoundForProductIdException(
    customerId: CustomerId,
    productId: ProductId,
) : DomainException("Customer with id '$customerId' does not have a subscription with product id '$productId'")
