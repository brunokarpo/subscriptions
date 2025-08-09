package nom.brunokarpo.subscriptions.application.usecases

interface UseCase<in Input, out Output> {
	fun execute(input: Input): Output
}