package kylelog.web

import kylelog.circuitbreaker.CircuitOpenException
import kylelog.circuitbreaker.fallback
import kylelog.circuitbreaker.fallbackIfOpen
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    /**
     * expect: "Success"
     */
    @GetMapping("/test/1")
    fun test1(): Result<Response> = circuit {
        Response("Success")
    }

    /**
     * expect: "Fallback"
     */
    @GetMapping("/test/2")
    fun test2(): Result<Response> = circuit {
        throw RuntimeException()
    }.fallback {
        Response("Fallback")
    }

    /**
     * expect: "Fallback"
     */
    @GetMapping("/test/3")
    fun test3(): Result<Response> = circuit {
        throw CircuitOpenException()
    }.fallback {
        Response("Fallback")
    }

    /**
     * expect: RuntimeException()
     */
    @GetMapping("/test/4")
    fun test4(): Result<Response> = circuit {
        throw RuntimeException()
    }.fallbackIfOpen {
        Response("Fallback")
    }

    /**
     * expect: "Fallback"
     */
    @GetMapping("/test/5")
    fun test5(): Result<Response> = circuit {
        throw CircuitOpenException()
    }.fallbackIfOpen {
        Response("Fallback")
    }
}

data class Response(val message: String)
