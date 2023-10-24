package kylelog.web

import kylelog.application.region.RegionFinder
import kylelog.application.user.UserFinder
import kylelog.library.circuitbreaker.fallback
import kylelog.library.circuitbreaker.fallbackIfOpen
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(
    val userFinder: UserFinder,
    val regionFinder: RegionFinder,
) {

    /**
     * GET /test/1?userId=1
     */
    @GetMapping("/test/1")
    fun testApi1(
        @RequestParam userId: Long
    ): Result<Response> = circuit {
        val user = userFinder.find(userId)
        Response(user.name)
    }

    /**
     * GET /test/2?userId=1
     */
    @GetMapping("/test/2")
    fun testApi2(
        @RequestParam userId: Long
    ): Result<Response> = circuit {
        val user = userFinder.find(userId)
        Response(user.name)
    }.fallback {
        Response("Fallback")
    }

    /**
     * GET /test/3?userId=1
     */
    @GetMapping("/test/3")
    fun testApi3(
        @RequestParam userId: Long
    ): Result<Response> = circuit {
        val user = userFinder.find(userId)
        Response(user.name)
    }.fallbackIfOpen {
        Response("Fallback")
    }

    /**
     * GET /test/4?userId=1&regionId=1
     */
    @GetMapping("/test/4")
    fun testApi4(
        @RequestParam userId: Long,
        @RequestParam regionId: Long,
    ): Response {
        val userName = circuit {
            userFinder.find(userId).name
        }.fallback {
            "karrot"
        }

        val regionName = circuit {
            regionFinder.find(regionId).name
        }.fallback {
            "seocho"
        }

        return Response("${userName.get()}, ${regionName.get()}")
    }
}

data class Response(val message: String)
