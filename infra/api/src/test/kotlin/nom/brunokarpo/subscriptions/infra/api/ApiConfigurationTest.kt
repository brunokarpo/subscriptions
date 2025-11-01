package nom.brunokarpo.subscriptions.infra.api

import com.ninjasquad.springmockk.MockkBean
import nom.brunokarpo.subscriptions.application.customer.ActivateCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.ActivateSubscriptionUseCase
import nom.brunokarpo.subscriptions.application.customer.CreateNewCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.DeactivateCustomerUseCase
import nom.brunokarpo.subscriptions.application.customer.RetrieveSubscriptionsByStatusUseCase
import nom.brunokarpo.subscriptions.application.customer.SubscribeProductToCustomerUseCase
import nom.brunokarpo.subscriptions.application.product.CreateNewProductUseCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [ApiConfiguration::class],
)
class ApiConfigurationTest {
    @MockkBean
    lateinit var createProductUseCase: CreateNewProductUseCase

    @MockkBean
    lateinit var createNewCustomerUseCase: CreateNewCustomerUseCase

    @MockkBean
    lateinit var subscribeProductToCustomerUseCase: SubscribeProductToCustomerUseCase

    @MockkBean
    lateinit var retrieveCustomersSubscriptionByStatusUseCase: RetrieveSubscriptionsByStatusUseCase

    @MockkBean
    lateinit var customerActivateUseCase: ActivateCustomerUseCase

    @MockkBean
    lateinit var activateSubscriptionUseCase: ActivateSubscriptionUseCase

    @MockkBean
    lateinit var deactivateCustomerUseCase: DeactivateCustomerUseCase

    @Autowired
    lateinit var client: WebTestClient

    @Test
    fun initContext() {
    }
}
