package cocomo.library.circuitbreaker

class ApplicationContextCircuitBreakerProvider(
    circuitBreaker: CircuitBreaker
) {
    init {
        Companion.circuitBreaker = circuitBreaker
    }

    companion object {
        private lateinit var circuitBreaker: CircuitBreaker

        fun get() = circuitBreaker
    }
}
