package kylelog.circuitbreaker

/**
 * @author kyle.kim@daangn.com
 */
class ApplicationContextCircuitBreakerProvider(
    circuitBreaker: CircuitBreaker
) {
    init {
        ApplicationContextCircuitBreakerProvider.circuitBreaker = circuitBreaker
    }

    companion object {
        private lateinit var circuitBreaker: CircuitBreaker

        fun get() = circuitBreaker
    }
}
