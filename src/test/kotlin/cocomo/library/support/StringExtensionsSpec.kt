package cocomo.library.support

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StringExtensionsSpec : FunSpec({

    test("Replace path-variable to '{id}'") {
        "api/v1/groups/1234".replacePathVariables() shouldBe "api/v1/groups/{id}"
        "api/v1/groups/1234/users/14123".replacePathVariables() shouldBe "api/v1/groups/{id}/users/{id}"
        "api/v23/groups/1234/users/14123".replacePathVariables() shouldBe "api/v23/groups/{id}/users/{id}"
        "api/v23-4/groups/1234/users/14123".replacePathVariables() shouldBe "api/v23-4/groups/{id}/users/{id}"
        "error".replacePathVariables() shouldBe "error"
        "internal/api/v23-4/groups/1234/users/14123".replacePathVariables() shouldBe "internal/api/v23-4/groups/{id}/users/{id}"
        "external/api/v23-4/groups/1234/users/14123".replacePathVariables() shouldBe "external/api/v23-4/groups/{id}/users/{id}"
    }

})
