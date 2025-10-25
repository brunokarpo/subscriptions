package nom.brunokarpo.subscriptions.application.customer

import nom.brunokarpo.subscriptions.application.usecases.UseCase

class ActivateSubscriptionUseCase : UseCase<ActivateSubscriptionUseCase.Input, ActivateSubscriptionUseCase.Output> {
    override suspend fun execute(input: Input): Output {
        TODO("Not yet implemented")
    }

    class Input(val customerId: String, val productId: String)
    class Output(val customerId: String, val productId: String, val status: String)
}