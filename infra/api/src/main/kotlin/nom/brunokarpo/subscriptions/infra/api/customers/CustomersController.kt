package nom.brunokarpo.subscriptions.infra.api.customers

import nom.brunokarpo.subscriptions.application.customer.ActivateSubscriptionUseCase
import nom.brunokarpo.subscriptions.application.customer.CreateNewCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.ActivateCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.DeactivateCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.RetrieveSubscriptionsByStatusUseCase
import nom.brunokarpo.subscriptions.application.customer.SubscribeProductToCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerByIdNotFoundException
import nom.brunokarpo.subscriptions.application.usecases.ApplicationException
import nom.brunokarpo.subscriptions.domain.common.DomainException
import nom.brunokarpo.subscriptions.domain.customer.exceptions.SubscriptionNotFoundForProductIdException
import nom.brunokarpo.subscriptions.infra.api.customers.CustomersController.Companion.BASE_URL
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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(BASE_URL)
class CustomersController(
    private val createNewCustomerUseCase: CreateNewCustomerUseCase,
    private val subscribeProductToCustomerUseCase: SubscribeProductToCustomerUseCase,
    private val retrieveCustomersSubscriptionByStatusUseCase: RetrieveSubscriptionsByStatusUseCase,
    private val customerActivateUseCase: ActivateCustomerUseCase,
    private val activateSubscriptionUseCase: ActivateSubscriptionUseCase,
    private val deactivateCustomerUseCase: DeactivateCustomerUseCase
) {

    companion object {
        const val BASE_URL = "/v1/customers"
        const val ACTIVATE_CUSTOMER = "/{customerId}/activate"
        const val DEACTIVATE_CUSTOMER = "/{customerId}/deactivate"
        const val SUBSCRIPTIONS = "/{customerId}/subscriptions"
        const val ACTIVATE_SUBSCRIPTION = "$SUBSCRIPTIONS/products/{productId}/activate"
    }

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

    @PatchMapping(ACTIVATE_CUSTOMER)
    suspend fun activateCustomer(
        @PathVariable customerId: String,
    ): ResponseEntity<ResponseCustomerDto> {
        val input = ActivateCustomerUseCase.Input(customerId = customerId)

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

    @PatchMapping(DEACTIVATE_CUSTOMER)
    suspend fun deactivateCustomer(
        @PathVariable customerId: String,
    ): ResponseEntity<ResponseCustomerDto> {
        val input = DeactivateCustomerUseCase.Input(customerId = customerId)

        val output = deactivateCustomerUseCase.execute(input)

        return ResponseEntity.ok(
            ResponseCustomerDto(
                id = output.customerId,
                name = output.name,
                email = output.email,
                active = output.active,
            ),
        )
    }

    @PostMapping(SUBSCRIPTIONS)
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

    @GetMapping(SUBSCRIPTIONS)
    suspend fun getCustomerSubscriptions(
        @PathVariable customerId: String,
        @RequestParam(required = true) status: String,
    ): ResponseEntity<ResponseSubscriptionsStatusDto> {
        val input = RetrieveSubscriptionsByStatusUseCase.Input(customerId = customerId, status = status)

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

    @PatchMapping(ACTIVATE_SUBSCRIPTION)
    suspend fun updateCustomerSubscription(
        @PathVariable customerId: String,
        @PathVariable productId: String,
    ): ResponseEntity<ResponseSubscriptionsStatusDto.SubscriptionStatusDto> {
        val input = ActivateSubscriptionUseCase.Input(customerId = customerId, productId = productId)
        val output = activateSubscriptionUseCase.execute(input)
        return ResponseEntity.ok(
            ResponseSubscriptionsStatusDto.SubscriptionStatusDto(
                customerId = output.customerId,
                productId = output.productId,
                status = output.status,
            ),
        )
    }

    @ExceptionHandler(CustomerByIdNotFoundException::class)
    fun handleApplicationException(ex: CustomerByIdNotFoundException): ResponseEntity<Map<String, String>> =
        ResponseEntity
            .status(
                HttpStatus.NOT_FOUND,
            ).body(
                mapOf("message" to ex.message!!),
            )

    @ExceptionHandler(SubscriptionNotFoundForProductIdException::class)
    fun handleApplicationException(ex: SubscriptionNotFoundForProductIdException): ResponseEntity<Map<String, String>> =
        ResponseEntity
            .status(
                HttpStatus.NOT_FOUND,
            ).body(
                mapOf("message" to ex.message!!),
            )

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(ex: ApplicationException): ResponseEntity<Map<String, String>> =
        ResponseEntity
            .status(
                HttpStatus.BAD_REQUEST,
            ).body(
                mapOf("message" to ex.message!!),
            )

    @ExceptionHandler(DomainException::class)
    fun handleApplicationException(ex: DomainException): ResponseEntity<Map<String, String>> =
        ResponseEntity
            .status(
                HttpStatus.BAD_REQUEST,
            ).body(
                mapOf("message" to ex.message!!),
            )
}
