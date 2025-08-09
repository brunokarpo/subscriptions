package nom.brunokarpo.subscriptions.infra.api

import com.ninjasquad.springmockk.MockkBean
import nom.brunokarpo.subscriptions.application.product.CreateNewProductUseCase
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	classes = [ApiConfiguration::class]
)
class ApiConfigurationTest {

	@MockkBean
	lateinit var createProductUseCase: CreateNewProductUseCase

	@Test
	fun initContext() {
	}
}