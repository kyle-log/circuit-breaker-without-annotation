package kylelog.web

import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

/**
 * This class is a controller advice that intercepts the responses of the controllers.
 * It checks if the returned object is a failure instance of `kotlin.Result`.
 * If it is, it extracts the exception contained in the failure
 * and throws it, allowing the exception to be handled by a global exception handler.
 */
@ControllerAdvice
class KotlinResultResponseBodyAdvice : ResponseBodyAdvice<Any> {

    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>,
    ) = returnType::class.java.isAssignableFrom(Result::class.java)

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse,
    ): Any? = when (body) {
        null -> null
        is Result<*> -> body.getOrThrow()
        else -> body
    }
}
