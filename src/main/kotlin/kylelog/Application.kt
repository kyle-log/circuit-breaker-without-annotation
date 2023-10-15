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
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import java.time.Duration
import java.util.function.Consumer

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Configuration
class CircuitBreakerConfiguration {

    @Bean
    fun customizer(): Customizer<Resilience4JCircuitBreakerFactory> {
        val timeLimiterConfig = TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(5))
            .cancelRunningFuture(true)
            .build()
        val circuitBreakerConfig = CircuitBreakerConfig.custom()
            .minimumNumberOfCalls(10)
            .slidingWindowSize(10)
            .failureRateThreshold(50f)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .enableAutomaticTransitionFromOpenToHalfOpen()
            .permittedNumberOfCallsInHalfOpenState(2)
            .maxWaitDurationInHalfOpenState(Duration.ofSeconds(10))
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

    @Bean
    fun applicationContextCircuitBreakerProvider(
        circuitBreaker: CircuitBreaker,
    ) = ApplicationContextCircuitBreakerProvider(
        circuitBreaker = circuitBreaker,
    )
}

@Configuration
class WebMvcConfiguration : WebMvcConfigurer {

    override fun addReturnValueHandlers(handlers: MutableList<HandlerMethodReturnValueHandler>) {
        handlers.add(ResultReturnValueHandler())
    }
}