package cocomo.library.circuitbreaker

import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory

class StandardCircuitBreaker(
    val factory: CircuitBreakerFactory<*, *>,
) : CircuitBreaker {

    override fun <T> run(name: String, block: () -> T): Result<T> = runCatching {
        factory.create(name).run(block) { e -> throw e.turnToOpenExceptionIfOpen() }
    }
}
