package nom.brunokarpo.subscriptions.application.fixtures

import nom.brunokarpo.subscriptions.domain.customer.Customer
import nom.brunokarpo.subscriptions.domain.customer.CustomerId

object CustomerFixture {

    const val CUSTOMER_NAME = "Default Name"
    const val CUSTOMER_EMAIL = "default@email.com"

    fun createCustomer(
        customerId: CustomerId = CustomerId.unique(),
        name: String = CUSTOMER_NAME,
        email: String = CUSTOMER_EMAIL,
        active: Boolean = false,
    ): Customer {
        val customer = Customer.create(
            id = customerId,
            name = name,
            email = email,
        )
        if (active) {
            customer.activate()
        }
        return customer
    }
}