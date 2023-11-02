package cocomo.circuitbreaker

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import cocomo.library.circuitbreaker.CircuitOpenException
import cocomo.library.circuitbreaker.fallback
import cocomo.library.circuitbreaker.fallbackIfOpen

class CircuitBreakerSpec : FunSpec({

    test("Should return success when fallback is called on successful result") {
        Result.success("Success")
            .fallback { "Fallback" }
            .getOrThrow() shouldBe "Success"
    }

    test("Should return fallback when fallback is called on failed result") {
        Result.failure<String>(RuntimeException())
            .fallback { "Fallback" }
            .getOrThrow() shouldBe "Fallback"
    }

    test("Should return fallback when fallback is called on CircuitOpenException") {
        Result.failure<String>(CircuitOpenException())
            .fallback { "Fallback" }
            .getOrThrow() shouldBe "Fallback"
    }

    test("Should return success when fallbackIfOpen is called on successful result") {
        Result.success("Success")
            .fallbackIfOpen { "Fallback" }
            .getOrThrow() shouldBe "Success"
    }

    test("Should throw RuntimeException when fallbackIfOpen is called on failed result") {
        shouldThrow<RuntimeException> {
            Result.failure<String>(RuntimeException())
                .fallbackIfOpen { "Fallback" }
                .getOrThrow()
        }
    }

    test("Should return fallback when fallbackIfOpen is called on CircuitOpenException") {
        Result.failure<String>(CircuitOpenException())
            .fallbackIfOpen { "Fallback" }
            .getOrThrow() shouldBe "Fallback"
    }
})
