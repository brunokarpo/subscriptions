package nom.brunokarpo.subscriptions.infra.api.customers

import nom.brunokarpo.subscriptions.application.customer.CreateNewCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.CustomerActivateUseCase
import nom.brunokarpo.subscriptions.application.customer.RetrieveCustomersSubscriptionsRequestedUseCase
import nom.brunokarpo.subscriptions.application.customer.SubscribeProductToCustomerUseCase
import nom.brunokarpo.subscriptions.application.usecases.ApplicationException
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.RequestCreateCustomerDto
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.RequestProductSubscriptionDto
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.ResponseCustomerDto
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.ResponseProductSubscriptionCustomerDto
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.ResponseSubscriptionsStatusDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/customers")
class CustomersController(
    private val createNewCustomerUseCase: CreateNewCustomerUseCase,
    private val subscribeProductToCustomerUseCase: SubscribeProductToCustomerUseCase,
    private val retrieveCustomersSubscriptionByStatusUseCase: RetrieveCustomersSubscriptionsRequestedUseCase,
    private val customerActivateUseCase: CustomerActivateUseCase,
) {
    @PostMapping
    suspend fun createCustomer(
        @RequestBody requestCreateCustomerDto: RequestCreateCustomerDto,
    ): ResponseEntity<ResponseCustomerDto> {
        val input =
            CreateNewCustomerUseCase.Input(
                name = requestCreateCustomerDto.name,
                email = requestCreateCustomerDto.email,
            )

        val output = createNewCustomerUseCase.execute(input)

        val responseCustomerDto =
            ResponseCustomerDto(
                id = output.id,
                name = output.name,
                email = output.email,
            )

        return ResponseEntity.status(201).body(responseCustomerDto)
    }

    @PostMapping("/{customerId}/products")
    suspend fun subscribeProductToCustomer(
        @RequestBody requestProductSubscriptionDto: RequestProductSubscriptionDto,
        @PathVariable customerId: String,
    ): ResponseEntity<ResponseProductSubscriptionCustomerDto> {
        val input =
            SubscribeProductToCustomerUseCase.Input(
                customerId = customerId,
                productName = requestProductSubscriptionDto.productName,
            )

        val output = subscribeProductToCustomerUseCase.execute(input)

        val customerDto =
            ResponseProductSubscriptionCustomerDto(
                email = output.email,
                productName = output.productName,
                subscriptionStatus = output.subscriptionStatus,
            )

        return ResponseEntity.status(201).body(customerDto)
    }

    @GetMapping("/{customerId}/subscriptions")
    suspend fun getCustomerSubscriptions(
        @PathVariable customerId: String,
        @RequestParam(required = true) status: String,
    ): ResponseEntity<ResponseSubscriptionsStatusDto> {
        val input = RetrieveCustomersSubscriptionsRequestedUseCase.Input(customerId = customerId, status = status)

        val output = retrieveCustomersSubscriptionByStatusUseCase.execute(input)

        val responseSubscriptionsStatusDto =
            ResponseSubscriptionsStatusDto(
                customerId = output.customerId,
                name = output.customerName,
                email = output.customerEmail,
                subscriptions =
                    output.subscriptions.map {
                        ResponseSubscriptionsStatusDto.SubscriptionStatusDto(
                            productId = it.productId,
                            status = it.status,
                        )
                    },
            )

        return ResponseEntity.ok(responseSubscriptionsStatusDto)
    }

    @PatchMapping("/{customerId}/activate")
    suspend fun activateCustomer(
        @PathVariable customerId: String,
    ): ResponseEntity<ResponseCustomerDto> {
        val input = CustomerActivateUseCase.Input(customerId = customerId)

        val output = customerActivateUseCase.execute(input)

        return ResponseEntity.ok(
            ResponseCustomerDto(
                id = output.customerId,
                name = output.name,
                email = output.email,
                activeUntil = output.activeUntil,
            ),
        )
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
