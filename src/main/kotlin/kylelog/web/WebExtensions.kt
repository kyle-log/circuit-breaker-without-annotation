package kylelog.web

import kylelog.library.circuitbreaker.ApplicationContextCircuitBreakerProvider
import kylelog.library.circuitbreaker.CircuitBreaker
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

fun <T> circuit(
    circuitBreaker: CircuitBreaker = ApplicationContextCircuitBreakerProvider.get(),
    name: String = path().getOrDefault("default-circuit-breaker"),
    f: () -> T,
): Result<T> = circuitBreaker.run(name, f)

private fun path(): Result<String> = when (val attributes = RequestContextHolder.currentRequestAttributes()) {
    is ServletRequestAttributes -> Result.success(attributes.request.servletPath)
    else -> Result.failure(IllegalStateException())
}

