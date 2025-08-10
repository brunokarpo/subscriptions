package nom.brunokarpo.subscriptions.infra.app.configurations

import nom.brunokarpo.subscriptions.application.customer.CreateNewCustomerUseCase
import nom.brunokarpo.subscriptions.application.product.CreateNewProductUseCase
import nom.brunokarpo.subscriptions.domain.customer.CustomerRepository
import nom.brunokarpo.subscriptions.domain.product.ProductRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseConfigurations {

	@Bean
	fun createNewProductUseCase(productRepository: ProductRepository): CreateNewProductUseCase {
		return CreateNewProductUseCase(productRepository)
	}

	@Bean
	fun createNewCustomerUseCase(customerRepository: CustomerRepository): CreateNewCustomerUseCase =
		CreateNewCustomerUseCase(customerRepository)

}