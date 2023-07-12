package jsondatabase.client

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.io.File


fun makeJsonString(args: Array<String>): String {
    return if (args[0] == "-in") {
        File("src/jsondatabase/client/data/" + args[1]).readText().trimIndent()
    } else {
        val request = Request(
            type = args[1],
            key = if (args.size > 2) JsonPrimitive(args[3]) else null,
            value = if (args.size > 4) JsonPrimitive(args[5]) else null
        )
        Gson().toJson(request).toString()
    }
}


data class Request(
    val type: String = "exit",
    val key: JsonElement? = null,
    val value: JsonElement? = null
)
