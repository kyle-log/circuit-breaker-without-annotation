package kylelog.web

import kylelog.circuitbreaker.CircuitOpenException
import kylelog.circuitbreaker.fallback
import kylelog.circuitbreaker.fallbackIfOpen
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @GetMapping("/test")
    fun test(
        @RequestParam status: CircuitStatus = CircuitStatus.CLOSED,
        @RequestParam needException: Boolean = false,
    ): Result<Response> = circuit {
        if (status == CircuitStatus.OPEN) {
            throw CircuitOpenException()
        }
        when (needException) {
            true -> throw RuntimeException()
            else -> Response("Success")
        }
    }

    @GetMapping("/test/fallback")
    fun testFallback(
        @RequestParam status: CircuitStatus = CircuitStatus.CLOSED,
        @RequestParam needException: Boolean = false,
    ): Result<Response> = circuit {
        if (status == CircuitStatus.OPEN) {
            throw CircuitOpenException()
        }
        when (needException) {
            true -> throw RuntimeException()
            else -> Response("Success")
        }
    }.fallback {
        Response("Fallback")
    }

    @GetMapping("/test/fallback-if-open")
    fun testFallbackIfOpen(
        @RequestParam status: CircuitStatus = CircuitStatus.CLOSED,
        @RequestParam needException: Boolean = false,
    ): Result<Response> = circuit {
        if (status == CircuitStatus.OPEN) {
            throw CircuitOpenException()
        }
        when (needException) {
            true -> throw RuntimeException()
            else -> Response("Success")
        }
    }.fallbackIfOpen {
        Response("Fallback")
    }
}

enum class CircuitStatus {
    OPEN,
    CLOSED,
}

data class Response(val message: String)
