package phonebook

import kotlin.system.measureNanoTime

fun doLinearSearch(names: ArrayList<String>, namesToFind: List<String>): Long {
    println("Start searching (linear search)...")
    var found = 0
    val timeLinearSearch = measureNanoTime {
        for (name in namesToFind) {
            val idx = linearSearch(names, name)
            if (idx != -1) ++found
        }
    }
    println(
        "Found $found / ${namesToFind.size} entries. Time taken: " +
                toTimeString(timeLinearSearch)
    )
    println()
    return timeLinearSearch
}

fun doJumpSearch(
    names: ArrayList<String>, namesToFind: List<String>,
    maxTime: Long = Long.MAX_VALUE
) {
    println("Start searching (bubble sort + jump search)...")
    val timeSorting = measureNanoTime { bubbleSort(names, maxTime) }
    var found = 0
    val timeSearching = measureNanoTime {
        if (timeSorting > maxTime) {
            for (name in namesToFind) {
                val idx = linearSearch(names, name)
                if (idx != -1) ++found
            }
        } else {
            for (name in namesToFind) {
                val idx = jumpSearch(names, name)
                if (idx != -1) ++found
            }
        }
    }
    val timeTotal = timeSorting + timeSearching
    println(
        "Found $found / ${namesToFind.size} entries. Time taken: " +
                toTimeString(timeTotal)
    )
    println("Sorting time: " + toTimeString(timeSorting))
    println("Searching time: " + toTimeString(timeSearching))
    println()
}

fun doBinarySearch(names: ArrayList<String>, namesToFind: List<String>) {
    println("Start searching (quick sort + binary search)...")
    val timeSorting = measureNanoTime { quickSort(names) }
    var found = 0
    val timeSearching = measureNanoTime {
        for (name in namesToFind) {
            val idx = binarySearch(names, name)
            if (idx != -1) ++found
        }
    }
    val timeTotal = timeSorting + timeSearching
    println(
        "Found $found / ${namesToFind.size} entries. Time taken: " +
                toTimeString(timeTotal)
    )
    println("Sorting time: " + toTimeString(timeSorting))
    println("Searching time: " + toTimeString(timeSearching))
    println()
}

fun doHaspTableSearch(names: ArrayList<String>, namesToFind: List<String>) {
    println("Start searching (hash table)...")
    val hashTable = mutableSetOf<String>()
    val timeCreating = measureNanoTime {
        names.forEach { name -> hashTable.add(name) }
    }
    var found = 0
    val timeSearching = measureNanoTime {
        for (name in namesToFind) {
            if (hashTable.contains(name)) ++found
        }
    }
    val timeTotal = timeCreating + timeSearching
    println(
        "Found $found / ${namesToFind.size} entries. Time taken: " +
                toTimeString(timeTotal)
    )
    println("Creating time: " + toTimeString(timeCreating))
    println("Searching time: " + toTimeString(timeSearching))
    println()
}

fun main() {
    val filepath = "/home/manuel/Desktop/kotlin_developer/phone_book/"
    val namesToFind = readSearchItems(filepath, "find.txt")
    val names = readDatabase(filepath, "directory.txt")
    val namesCopy = readDatabase(filepath, "directory.txt")

    val timeLinearSearch = doLinearSearch(names, namesToFind)
    doJumpSearch(names, namesToFind, 10 * timeLinearSearch)
    doBinarySearch(namesCopy, namesToFind)
    doHaspTableSearch(names, namesToFind)
}
