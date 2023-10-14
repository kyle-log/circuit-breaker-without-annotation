package kylelog.circuitbreaker

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory

/**
 * @author kyle.kim@daangn.com
 */
class StandardCircuitBreaker(
    val factory: CircuitBreakerFactory<*, *>,
) : CircuitBreaker {

    override fun <T> run(name: String, block: () -> T): Result<T> = runCatching {
        factory.create(name).run(block) { error ->
            throw when (error) {
                is CallNotPermittedException -> CircuitOpenException()
                else -> error
            }
        }
    }
}
