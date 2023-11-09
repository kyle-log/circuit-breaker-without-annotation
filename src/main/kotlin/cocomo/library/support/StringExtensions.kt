package cocomo.library.support

private val regexNumber = Regex("^\\d*$")

fun String.isNumber(): Boolean = this.isNotBlank() && regexNumber.matches(this)

fun String.replacePathVariables(to: String = "{id}"): String = this
    .split("/")
    .joinToString("/") {
        when (it.isNumber()) {
            true -> to
            false -> it
        }
    }