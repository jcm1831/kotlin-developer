package jsondatabase.server

enum class Response {
    OKAY, ERROR, VALUE;
}

fun makeResponse(response: Response, value: String = ""): String {
    return when (response) {
        Response.OKAY -> """{"response":"OK"}""".trimIndent()
        Response.ERROR -> """{"response":"ERROR","reason":"No such key"}""".trimIndent()
        Response.VALUE -> """{"response":"OK","value":$value}""".trimIndent()
    }
}