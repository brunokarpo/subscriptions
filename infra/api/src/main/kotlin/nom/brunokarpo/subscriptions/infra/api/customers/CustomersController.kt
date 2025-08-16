package nom.brunokarpo.subscriptions.infra.api.customers

import nom.brunokarpo.subscriptions.application.customer.CreateNewCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.exceptions.CustomerUniqueEmailException
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.CreateCustomerDto
import nom.brunokarpo.subscriptions.infra.api.customers.dtos.CustomerDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/customers")
class CustomersController(
	private val createNewCustomerUseCase: CreateNewCustomerUseCase
) {

	@PostMapping
	suspend fun createCustomer(@RequestBody createCustomerDto: CreateCustomerDto): ResponseEntity<CustomerDto> {
		val input = CreateNewCustomerUseCase.Input(
			name = createCustomerDto.name,
			email = createCustomerDto.email
		)

		val output = createNewCustomerUseCase.execute(input)

		val customerDto = CustomerDto(
			id = output.id,
			name = output.name,
			email = output.email
		)

		return ResponseEntity.status(201).body(customerDto)
	}

	@ExceptionHandler(CustomerUniqueEmailException::class)
	suspend fun handleCustomerUniqueEmailException(
		ex: CustomerUniqueEmailException
	): ResponseEntity<Map<String, String>> =
		ResponseEntity.status(
			HttpStatus.BAD_REQUEST
		).body(
			mapOf("message" to ex.message!!)
		)
}