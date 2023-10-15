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
        @RequestParam status: Int?
    ): Result<Response> = circuit {
        when (status) {
            1 -> Response("Success")
            2 -> throw RuntimeException()
            else -> throw CircuitOpenException()
        }
    }

    @GetMapping("/test/fallback")
    fun testFallback(
        @RequestParam status: Int?
    ): Result<Response> = circuit {
        when (status) {
            1 -> Response("Success")
            2 -> throw RuntimeException()
            else -> throw CircuitOpenException()
        }
    }.fallback {
        Response("Fallback")
    }

    @GetMapping("/test/fallback-if-open")
    fun testFallbackIfOpen(
        @RequestParam status: Int?
    ): Result<Response> = circuit {
        when (status) {
            1 -> Response("Success")
            2 -> throw RuntimeException()
            else -> throw CircuitOpenException()
        }
    }.fallbackIfOpen {
        Response("Fallback")
    }
}

data class Response(val message: String)
