package kylelog.web

import kylelog.circuitbreaker.ApplicationContextCircuitBreakerProvider
import kylelog.circuitbreaker.CircuitBreaker
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * @author kyle.kim@daangn.com
 */
fun <T> circuit(
    circuitBreaker: CircuitBreaker = ApplicationContextCircuitBreakerProvider.get(),
    name: String = path().getOrDefault("default-circuit-breaker"),
    f: () -> T,
): Result<T> = circuitBreaker.run(name, f)

fun path(): Result<String> = when (val attributes = RequestContextHolder.currentRequestAttributes()) {
    is ServletRequestAttributes -> Result.success(attributes.request.servletPath)
    else -> Result.failure(IllegalStateException("Can't access: $attributes"))
}