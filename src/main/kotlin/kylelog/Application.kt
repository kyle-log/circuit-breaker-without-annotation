package kylelog

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import io.github.resilience4j.timelimiter.TimeLimiterRegistry
import kylelog.circuitbreaker.ApplicationContextCircuitBreakerProvider
import kylelog.circuitbreaker.CircuitBreaker
import kylelog.circuitbreaker.StandardCircuitBreaker
import kylelog.web.ResultReturnValueHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.runApplication
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration
import java.util.function.Consumer

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Configuration
class Configuration {

    @Bean
    fun applicationContextCircuitBreakerProvider(
        circuitBreaker: CircuitBreaker,
    ) = ApplicationContextCircuitBreakerProvider(
        circuitBreaker = circuitBreaker,
    )
}

@Configuration
class CircuitBreakerConfiguration {

    @Bean
    fun customizer(): Customizer<Resilience4JCircuitBreakerFactory> {
        val timeLimiterConfig = TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(10))
            .cancelRunningFuture(true)
            .build()
        val circuitBreakerConfig = CircuitBreakerConfig.custom()
            .minimumNumberOfCalls(100) // 최소 100번 호출
            .slidingWindowSize(100) // 100개의 요청을 기준으로
            .failureRateThreshold(80f) // 80% 이상 실패하면
            .waitDurationInOpenState(Duration.ofSeconds(30)) // 30초간 open
            .enableAutomaticTransitionFromOpenToHalfOpen() // 하프오픈 이용하기
            .permittedNumberOfCallsInHalfOpenState(10) // 10번의 하프오픈 허용
            .maxWaitDurationInHalfOpenState(Duration.ofSeconds(10)) // 하프오픈 최대 10초 대기
            .build()
        return Customizer { factory ->
            factory.configureDefault { id ->
                Resilience4JConfigBuilder(id)
                    .timeLimiterConfig(timeLimiterConfig)
                    .circuitBreakerConfig(circuitBreakerConfig)
                    .build()
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean(CircuitBreakerFactory::class)
    fun resilience4jCircuitBreakerFactory(
        customizers: List<Customizer<Resilience4JCircuitBreakerFactory>> = ArrayList()
    ): Resilience4JCircuitBreakerFactory {
        val factory = Resilience4JCircuitBreakerFactory(
            CircuitBreakerRegistry.ofDefaults(),
            TimeLimiterRegistry.ofDefaults(),
            null
        )
        customizers.forEach(
            Consumer { customizer: Customizer<Resilience4JCircuitBreakerFactory> ->
                customizer.customize(
                    factory
                )
            }
        )
        return factory
    }

    @Bean
    fun circuitBreaker(
        circuitBreakerFactory: CircuitBreakerFactory<*, *>
    ) = StandardCircuitBreaker(
        factory = circuitBreakerFactory
    )
}

@Configuration
class WebMvcConfiguration : WebMvcConfigurer {

    override fun addReturnValueHandlers(handlers: MutableList<HandlerMethodReturnValueHandler>) {
        handlers.add(ResultReturnValueHandler())
    }
}