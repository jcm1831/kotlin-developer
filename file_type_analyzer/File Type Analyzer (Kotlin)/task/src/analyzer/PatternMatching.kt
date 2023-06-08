package analyzer

fun regexMatch(pattern: String, str: String): IntArray {
    return pattern.toRegex().findAll(str).map { it.range.first }.toList()
        .toIntArray()
}

fun prefixFunction(str: String): IntArray {
    val prefix = IntArray(str.length)
    for (i in 1 until str.length) {
        var j = prefix[i - 1]
        while (true) {
            if (str[i] == str[j]) {
                prefix[i] = j + 1
                break
            } else {
                if (j == 0) break
                j = prefix[j - 1]
            }
        }
    }
    return prefix
}

fun kmpMatch(pattern: String, str: String): IntArray {
    val occurrences = arrayListOf<Int>()
    val prefix = prefixFunction(pattern)
    var shift = 0
    while ((shift + pattern.length) <= str.length) {
        val substr = str.substring(shift, shift + pattern.length)
        var matchLength = pattern.length
        for (i in substr.indices) {
            if (pattern[i] != substr[i]) {
                matchLength = i
                break
            }
        }
        if (matchLength == pattern.length) occurrences.add(shift)
        shift += if (matchLength == 0) 1 else matchLength - prefix[matchLength - 1]
    }
    return occurrences.toIntArray()
}

fun simpleMatch(pattern: String, str: String): IntArray {
    val occurrences = arrayListOf<Int>()
    var shift = 0
    while ((shift + pattern.length) <= str.length) {
        val substr = str.substring(shift, shift + pattern.length)
        if (pattern == substr) occurrences.add(shift)
        ++shift
    }
    return occurrences.toIntArray()
}

fun Long.pow(n : Int) : Long {
    var pow = 1L
    repeat(n) { pow *= this }
    return pow
}

fun polynomialHash(str: String, a: Int = 3, m: Int = 11): Int {
    var hash = 0L
    var aPow = 1L
    for (letter in str) {
        hash += letter.code.toLong() * aPow
        aPow *= a
    }
    return hash.mod(m)
}

fun rollingPolynomialHash(
    prevHash: Int,
    length: Int, sj: Int, si: Int,
    a: Int = 3, m: Int = 11
): Int {
    val aPow = a.toLong().pow(length - 1)
    return ((prevHash.toLong() - sj.toLong() * aPow) * a.toLong() + si.toLong()).mod(m)
}

fun rabinKarpMatch(pattern: String, str: String): IntArray {
    val occurrences = arrayListOf<Int>()

    val patternHash = polynomialHash(pattern)

    var shift = str.length - pattern.length
    var substr = str.substring(shift, shift + pattern.length)
    var substrHash = polynomialHash(substr)

    while (true) {
        if (substrHash == patternHash && pattern == substr) {
            occurrences.add(shift)
        }
        --shift
        if (shift < 0) break
        substrHash = rollingPolynomialHash(
            substrHash,
            pattern.length, substr.last().code, str[shift].code
        )
        substr = str.substring(shift, shift + pattern.length)
    }

    return occurrences.toIntArray()
}

fun makeMatchingAlgorithm(algo: String): ((String, String) -> IntArray)? {
    return when (algo) {
        "--naive" -> ::simpleMatch
        "--KMP" -> ::kmpMatch
        "--regex" -> ::regexMatch
        "--rabinKarp" -> ::rabinKarpMatch
        else -> null
    }
}