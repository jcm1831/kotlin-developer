package phonebook

import java.io.File

fun readSearchItems(filepath: String, filename: String): List<String> {
    return File(filepath + filename).readLines()
}

fun readDatabase(filepath: String, filename: String): ArrayList<String> {
    val names = arrayListOf<String>()
    File(filepath + filename).forEachLine {
        val (_, name) = it.split(" ", limit = 2)
        names.add(name)
    }
    return names
}

fun toTimeString(timeInNanos: Long): String {
    val timeInMillis = timeInNanos / 1_000_000
    val millis = timeInMillis % 1_000
    val sec = (timeInMillis / 1_000) % 60
    val min = timeInMillis / (60 * 1_000)
    return "$min min. $sec sec. $millis ms."
}