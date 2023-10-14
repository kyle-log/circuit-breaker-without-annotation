package kylelog.circuitbreaker

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StandardCircuitBreakerSpec : FunSpec({

    val closedCircuitBreaker = StandardCircuitBreaker(
        factory = FakeAlwaysClosedCircuitBreakerFactory(),
    )
    val openCircuitBreaker = StandardCircuitBreaker(
        factory = FakeAlwaysOpenCircuitBreakerFactory(),
    )
    val name = "test"

    test("Success when run on closed circuit breaker") {
        closedCircuitBreaker.run(name) {
            "Success"
        }.getOrThrow() shouldBe "Success"
    }

    test("RuntimeException when throw runtime exception on closed circuit breaker") {
        shouldThrow<RuntimeException> {
            val block: () -> String = {
                throw RuntimeException()
            }
            closedCircuitBreaker.run(name) {
                block()
            }.getOrThrow()
        }
    }

    test("CircuitOpenException when run on open circuit breaker") {
        shouldThrow<CircuitOpenException> {
            openCircuitBreaker.run(name) {
                "Success"
            }.getOrThrow()
        }
    }

    test("CircuitOpenException when throw runtime exception on open circuit breaker") {
        shouldThrow<CircuitOpenException> {
            val f: () -> String = {
                throw RuntimeException()
            }
            openCircuitBreaker.run(name) {
                f()
            }.getOrThrow()
        }
    }

})