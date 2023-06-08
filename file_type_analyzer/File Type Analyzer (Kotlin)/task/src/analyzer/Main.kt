package analyzer

import java.io.File

fun main(args: Array<String>) {
    // decompose cli arguments
    val (folderPath, patternDatabase) = args

    // load data base for patterns
    val patterns = arrayListOf<Pattern>()
    if (File(patternDatabase).exists()) {
        File(patternDatabase).forEachLine { line ->
            val (priority, pattern, result) = line.split(";")
            patterns.add(
                Pattern(
                    priority.toInt(), pattern.trim('"'),
                    result.trim('"')
                )
            )
        }
    }

    // match patterns in files
    if (File(folderPath).isDirectory) {
        val workers = mutableListOf<Worker>()
        File(folderPath).listFiles()?.forEach {
            workers.add(Worker(folderPath + "/" + it.name, patterns))
        }
        workers.forEach { it.start() }
        workers.forEach { it.join() }
    } else {
        println("ERROR: The given path is no directory.")
    }
}