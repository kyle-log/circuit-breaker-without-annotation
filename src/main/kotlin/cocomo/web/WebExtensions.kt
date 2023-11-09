package cocomo.web

import cocomo.library.circuitbreaker.ApplicationContextCircuitBreakerProvider
import cocomo.library.circuitbreaker.CircuitBreaker
import cocomo.library.support.replacePathVariables
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

internal fun <T> circuit(
    circuitBreaker: CircuitBreaker = ApplicationContextCircuitBreakerProvider.get(),
    name: String = path().getOrDefault("default-circuit-breaker").replacePathVariables(),
    f: () -> T,
): Result<T> = circuitBreaker.run(name, f)

private fun path(): Result<String> = when (val attributes = RequestContextHolder.currentRequestAttributes()) {
    is ServletRequestAttributes -> Result.success(attributes.request.servletPath)
    else -> Result.failure(IllegalStateException())
}

fun <T> Result<T>.get() = this.getOrThrow()
