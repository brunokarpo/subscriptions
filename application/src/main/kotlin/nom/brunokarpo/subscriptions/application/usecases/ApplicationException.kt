package nom.brunokarpo.subscriptions.application.usecases

abstract class ApplicationException(
    message: String,
) : RuntimeException(message, null, true, false)
