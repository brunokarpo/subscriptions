package nom.brunokarpo.subscriptions.infra.app.configurations

import nom.brunokarpo.subscriptions.application.customer.CreateNewCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.ActivateCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.ActivateSubscriptionUseCase
import nom.brunokarpo.subscriptions.application.customer.RetrieveSubscriptionsByStatusUseCase
import nom.brunokarpo.subscriptions.application.customer.SubscribeProductToCustomerUseCase
import nom.brunokarpo.subscriptions.application.product.CreateNewProductUseCase
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.domain.product.ProductRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseConfigurations {
    @Bean
    fun createNewProductUseCase(productRepository: ProductRepository): CreateNewProductUseCase =
        CreateNewProductUseCase(productRepository = productRepository)

    @Bean
    fun createNewCustomerUseCase(customerRepository: CustomerRepository): CreateNewCustomerUseCase =
        CreateNewCustomerUseCase(customerRepository = customerRepository)

    @Bean
    fun subscribeProductToCustomerUseCase(
        customerRepository: CustomerRepository,
        productRepository: ProductRepository,
    ) = SubscribeProductToCustomerUseCase(customerRepository = customerRepository, productRepository = productRepository)

    @Bean
    fun retrieveCustomersSubscriptionByStatusUseCase(customerRepository: CustomerRepository) =
        RetrieveSubscriptionsByStatusUseCase(customerRepository = customerRepository)

    @Bean
    fun customerActivateUseCase(customerRepository: CustomerRepository) = ActivateCustomerUseCase(customerRepository = customerRepository)

    @Bean
    fun activateSubscriptionUseCase(customerRepository: CustomerRepository) =
        ActivateSubscriptionUseCase(customerRepository = customerRepository)
}
