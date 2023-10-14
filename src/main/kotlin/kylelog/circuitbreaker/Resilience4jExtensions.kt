package kylelog.circuitbreaker

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import kylelog.circuitbreaker.CircuitOpenException

/**
 * Extensions for Resilience4j Dependencies
 */
fun Throwable.turnToOpenExceptionIfOpen(): Throwable = when (this) {
    is CallNotPermittedException -> CircuitOpenException()
    else -> this
}

fun callNotPermittedException(name: String): Throwable = CallNotPermittedException
    .createCallNotPermittedException(CircuitBreaker.ofDefaults(name))