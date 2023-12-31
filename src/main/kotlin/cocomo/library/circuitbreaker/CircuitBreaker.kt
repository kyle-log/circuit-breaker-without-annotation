package cocomo.library.circuitbreaker

interface CircuitBreaker {
    fun <T> run(name: String, block: () -> T): Result<T>
}

class CircuitOpenException(message: String = "Circuit breaker is open") : RuntimeException(message)

fun <T> Result<T>.fallback(f: () -> T): Result<T> = when (this.isSuccess) {
    true -> this
    else -> runCatching { f() }
}

fun <T> Result<T>.fallbackIfOpen(f: () -> T): Result<T> = when (this.exceptionOrNull()) {
    is CircuitOpenException -> runCatching { f() }
    else -> this
}
