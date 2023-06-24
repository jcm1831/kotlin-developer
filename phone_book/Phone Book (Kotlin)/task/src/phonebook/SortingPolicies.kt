package phonebook

import kotlin.system.measureNanoTime

fun <T : Comparable<T>> bubbleSort(
    arr: ArrayList<T>,
    maxTime: Long = Long.MAX_VALUE
) {
    var time = 0L
    repeat(arr.size) {
        time += measureNanoTime {
            for (i in 0 until arr.size - 1) {
                if (arr[i] > arr[i + 1]) {
                    arr[i + 1] = arr[i].also { arr[i] = arr[i + 1] }
                }
            }
        }
        if (time > maxTime) return
    }
}

fun <T : Comparable<T>> quickSort(
    arr: ArrayList<T>,
    from: Int = 0,
    to: Int = arr.size - 1
) {
    if (from < to) {
        val pivotIndex = divideIntoParts(arr, from, to)
        quickSort(arr, from, pivotIndex - 1)
        quickSort(arr, pivotIndex + 1, to)
    }
}

fun <T : Comparable<T>> divideIntoParts(
    arr: ArrayList<T>,
    from: Int,
    to: Int
): Int {
    val pivot = arr[to]
    var left = from
    var right = to - 1
    while (left < right) {
        // get left element that is bigger than pivot
        while (left < right && arr[left] <= pivot) {
            ++left
        }
        // get right element that is smaller than or equal to pivot
        while (right > left && arr[right] > pivot) {
            --right
        }
        // swap
        if (arr[left] > arr[right]) {
            arr[right] = arr[left].also { arr[left] = arr[right] }
        }
    }
    // swap pivot element to final position
    if (arr[left] > pivot) {
        arr[to] = arr[left].also { arr[left] = arr[to] }
    } else {
        left = to
    }
    return left
}