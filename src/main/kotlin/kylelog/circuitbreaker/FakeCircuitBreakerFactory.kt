package kylelog.circuitbreaker

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.ConfigBuilder
import java.util.function.Function
import java.util.function.Supplier

/**
 * @author kyle.kim@daangn.com
 */
class FakeAlwaysClosedCircuitBreakerFactory : CircuitBreakerFactory<FakeConfig, FakeConfigBuilder>() {

    override fun configBuilder(id: String): FakeConfigBuilder {
        return FakeConfigBuilder()
    }

    override fun create(id: String): CircuitBreaker {
        return FakeAlwaysClosedCircuitBreaker()
    }

    override fun configureDefault(defaultConfiguration: Function<String, FakeConfig>) {
        // do nothing
    }
}

class FakeAlwaysClosedCircuitBreaker : CircuitBreaker {
    override fun <T : Any> run(toRun: Supplier<T>, fallback: Function<Throwable, T>): T {
        val result = runCatching { toRun.get() }
        return when (val e = result.exceptionOrNull()) {
            null -> result.getOrThrow()
            else -> fallback.apply(e)
        }
    }
}

class FakeAlwaysOpenCircuitBreakerFactory : CircuitBreakerFactory<FakeConfig, FakeConfigBuilder>() {

    override fun configBuilder(id: String): FakeConfigBuilder {
        return FakeConfigBuilder()
    }

    override fun create(id: String): CircuitBreaker {
        return FakeAlwaysOpenCircuitBreaker()
    }

    override fun configureDefault(defaultConfiguration: Function<String, FakeConfig>) {
        // do nothing
    }
}

class FakeConfig

class FakeConfigBuilder : ConfigBuilder<FakeConfig> {
    override fun build(): FakeConfig {
        return FakeConfig()
    }
}

class FakeAlwaysOpenCircuitBreaker : CircuitBreaker {
    override fun <T : Any> run(toRun: Supplier<T>, fallback: Function<Throwable, T>): T {
        return fallback.apply(openException())
    }

    private fun openException() = CallNotPermittedException.createCallNotPermittedException(
        io.github.resilience4j.circuitbreaker.CircuitBreaker.ofDefaults(
            "fake"
        )
    )
}
