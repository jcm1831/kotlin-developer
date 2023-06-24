package phonebook

import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

fun <T> linearSearch(
    arr: ArrayList<T>, toSearch: T,
    from: Int = 0, to: Int = arr.size - 1
): Int {
    if (arr.isEmpty()) return -1
    for (i in to downTo from) {
        if (arr[i] == toSearch) return i
    }
    return -1
}

fun <T : Comparable<T>> jumpSearch(arr: ArrayList<T>, toSearch: T): Int {
    if (arr.isEmpty()) return -1

    val blockSize = floor(sqrt(arr.size.toDouble())).toInt()
    val last = arr.size - 1
    var curr = 0
    var prev = 0

    // find block
    while (arr[curr] < toSearch) {
        if (curr == last) return -1
        prev = curr
        curr = min(curr + blockSize, last)
    }

    // perform backwards linear search in block
    curr = linearSearch(arr, toSearch, prev, curr)

    // return index if there is a match
    if (arr[curr] == toSearch) return curr

    return -1
}

fun <T : Comparable<T>> binarySearch(arr: ArrayList<T>, toSearch: T): Int {
    var left = 0
    var right = arr.size - 1
    while (left <= right) {
        val middle = (left + right) / 2
        if (arr[middle] == toSearch) {
            return middle
        } else if (arr[middle] > toSearch) {
            right = middle - 1
        } else {
            left = middle + 1
        }
    }
    return -1
}