package nom.brunokarpo.subscriptions.infra.api.customers

import nom.brunokarpo.subscriptions.application.customer.CreateNewCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.SubscribeProductToCustomerUseCase
import nom.brunokarpo.subscriptions.application.usecases.ApplicationException
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.CreateCustomerDto
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.CustomerDto
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.ProductSubscriptionCustomerDto
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.ProductSubscriptionDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/customers")
class CustomersController(
    private val createNewCustomerUseCase: CreateNewCustomerUseCase,
    private val subscribeProductToCustomerUseCase: SubscribeProductToCustomerUseCase,
) {
    @PostMapping
    suspend fun createCustomer(
        @RequestBody createCustomerDto: CreateCustomerDto,
    ): ResponseEntity<CustomerDto> {
        val input =
            CreateNewCustomerUseCase.Input(
                name = createCustomerDto.name,
                email = createCustomerDto.email,
            )

        val output = createNewCustomerUseCase.execute(input)

        val customerDto =
            CustomerDto(
                id = output.id,
                name = output.name,
                email = output.email,
            )

        return ResponseEntity.status(201).body(customerDto)
    }

    @PostMapping("/{customerId}/products")
    suspend fun subscribeProductToCustomer(
        @RequestBody productSubscriptionDto: ProductSubscriptionDto,
        @PathVariable customerId: String,
    ): ResponseEntity<ProductSubscriptionCustomerDto> {
        val input =
            SubscribeProductToCustomerUseCase.Input(
                customerId = customerId,
                productName = productSubscriptionDto.productName,
            )

        val output = subscribeProductToCustomerUseCase.execute(input)

        val customerDto =
            ProductSubscriptionCustomerDto(
                email = output.email,
                productName = output.productName,
                subscriptionStatus = output.subscriptionStatus,
            )

        return ResponseEntity.status(201).body(customerDto)
    }

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(ex: ApplicationException): ResponseEntity<Map<String, String>> =
        ResponseEntity
            .status(
                HttpStatus.BAD_REQUEST,
            ).body(
                mapOf("message" to ex.message!!),
            )
}
