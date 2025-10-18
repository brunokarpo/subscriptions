package nom.brunokarpo.subscriptions.application.usecases

interface UseCase<in Input, out Output> {
    suspend fun execute(input: Input): Output
}
