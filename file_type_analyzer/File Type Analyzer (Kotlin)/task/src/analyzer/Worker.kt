package analyzer

import java.io.File

class Worker(
    private val filename: String,
    private val patterns: ArrayList<Pattern>,
    private val algo: String = "--rabinKarp"
) : Thread() {
    override fun run() {
        val matchingAlgo = makeMatchingAlgorithm(algo)
        val matches = arrayListOf<Pattern>()
        for (pattern in patterns) {
            val stream = File(filename).inputStream()
            val byteBuffer = ByteArray(1024)
            while (stream.read(byteBuffer) != -1) {
                val indices = matchingAlgo!!(pattern.value, byteBuffer.decodeToString())
                if (indices.isNotEmpty()) {
                    matches.add(pattern)
                    break
                }
            }
            stream.close()
        }
        println(
            "$filename: " + (matches.maxByOrNull { it.priority }?.result
                ?: "Unknown file type")
        )
    }
}