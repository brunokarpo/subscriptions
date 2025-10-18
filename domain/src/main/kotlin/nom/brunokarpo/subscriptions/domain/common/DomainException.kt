package nom.brunokarpo.subscriptions.domain.common

abstract class DomainException(
    message: String,
    throwable: Throwable? = null,
) : RuntimeException(message, throwable, true, false)
