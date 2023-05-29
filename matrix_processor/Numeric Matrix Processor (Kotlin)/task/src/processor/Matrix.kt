package processor

infix fun MutableList<Double>.dot(other: MutableList<Double>): Double {
    var out = 0.0
    for (i in 0 until size) out += this[i] * other[i]
    return out
}

fun MutableList<MutableList<Double>>.copy(): MutableList<MutableList<Double>> {
    val copy: MutableList<MutableList<Double>> = mutableListOf()
    for (i in 0 until this.size) {
        copy.add(mutableListOf())
        for (j in 0 until this[i].size) {
            copy[i].add(this[i][j])
        }
    }
    return copy
}

class Matrix {
    private var values: MutableList<MutableList<Double>> = mutableListOf()
    private var numRows: Int = 0
    private var numColumns: Int = 0

    constructor(
        values: MutableList<MutableList<Double>>,
        numRows: Int,
        numColumns: Int
    ) {
        this.values = values
        this.numRows = numRows
        this.numColumns = numColumns
    }

    constructor(input: String = " ") {
        print("Enter size of${input}matrix: ")
        apply {
            val (n, m) = readln().split(" ").map { it.toInt() }
            numRows = n
            numColumns = m
        }
        println("Enter${input}matrix:")
        repeat(numRows) {
            val row = readln().split(" ").map { it.toDouble() }.toMutableList()
            try {
                require(row.size == numColumns)
                values.add(row)
            } catch (e: Exception) {
                println("ERROR: Row size is ${row.size} but must be equal to $numColumns")
            }
        }
    }

    constructor(numRows: Int, numColumns: Int) {
        this.numRows = numRows
        this.numColumns = numColumns
        repeat(numRows) { values.add(MutableList(numColumns) { 0.0 }) }
    }

    operator fun get(i: Int, j: Int) = values[i][j]
    operator fun set(i: Int, j: Int, a: Double) {
        values[i][j] = a
    }

    operator fun set(i: Int, row: MutableList<Double>) {
        values[i] = row
    }

    private fun getRow(rowIndex: Int) = values[rowIndex]
    private fun getColumn(columnIndex: Int): MutableList<Double> {
        val column = mutableListOf<Double>()
        values.forEach { row ->
            row.forEachIndexed { index, d ->
                if (index == columnIndex) column.add(d)
            }
        }
        return column
    }

    fun calculateDeterminant(): Double? {
        return try {
            require(numRows == numColumns)
            if (numRows == 2 && numColumns == 2) {
                return (values[0][0] * values[1][1]) -
                        (values[0][1] * values[1][0])
            } else {
                var result = 0.0
                for (j in 0 until numColumns) {
                    // recursive call-chain!
                    val sign = if ((j + 2) % 2 == 0) +1 else -1
                    result += values[0][j] * sign * getMinor(
                        0,
                        j
                    ).calculateDeterminant()!!
                }
                return result
            }
        } catch (ex: Exception) {
            println("The operation cannot be performed.")
            null
        }
    }

    fun inverseMatrix(): Matrix? {
        return try {
            require(numRows == numColumns)
            val det = calculateDeterminant()!!
            return if (det == 0.0) {
                println("This matrix doesn't have an inverse.")
                null
            } else {
                val cofactors =
                    getCofactorMatrix().transposeAlongMainDiagonal()!!
                cofactors * (1 / det)
            }
        } catch (ex: Exception) {
            println("The operation cannot be performed.")
            null
        }
    }

    private fun getMinor(rowIndex: Int, columnIndex: Int): Matrix {
        // make a deep copy
        val minor = values.copy()
        // delete row
        minor.removeAt(rowIndex)
        // delete column
        minor.forEach { it.removeAt(columnIndex) }
        // return reduced matrix of minor
        return Matrix(minor, numRows - 1, numColumns - 1)
    }

    private fun getCofactorMatrix(): Matrix {
        val cofactors = Matrix(numRows, numColumns)
        for (i in 0 until numRows) {
            for (j in 0 until numColumns) {
                // recursive call-chain!
                val sign = if ((j + i + 2) % 2 == 0) +1 else -1
                cofactors[i, j] += sign * getMinor(
                    i,
                    j
                ).calculateDeterminant()!!
            }
        }
        return cofactors
    }

    fun transposeAlongMainDiagonal(): Matrix? {
        return try {
            require(numRows == numColumns)
            val result = Matrix(numRows, numColumns)
            for (j in 0 until numColumns) {
                val row = getRow(j)
                for (i in 0 until numRows) {
                    result[i, j] = row[i]
                }
            }
            result
        } catch (ex: Exception) {
            println("The operation cannot be performed.")
            null
        }
    }

    fun transposeAlongSideDiagonal(): Matrix? {
        return try {
            require(numRows == numColumns)
            val result = Matrix(numRows, numColumns)
            for (i in 0 until numRows) {
                val column = getColumn(numColumns - (i + 1)).reversed()
                result[i] = column.toMutableList()
            }
            result
        } catch (ex: Exception) {
            println("The operation cannot be performed.")
            null
        }
    }

    fun transposeAlongVerticalLine(): Matrix? {
        return try {
            require(numRows == numColumns)
            val result = Matrix(numRows, numColumns)
            for (i in 0 until numRows) {
                result[i] = values[i].reversed().toMutableList()
            }
            result
        } catch (ex: Exception) {
            println("The operation cannot be performed.")
            null
        }
    }

    fun transposeAlongHorizontalLine(): Matrix? {
        return try {
            require(numRows == numColumns)
            val result = Matrix(numRows, numColumns)
            for (i in 0 until numRows / 2) {
                result[i] = getRow(numRows - (i + 1))
                result[numRows - (i + 1)] = getRow(i)
            }
            result
        } catch (ex: Exception) {
            println("The operation cannot be performed.")
            null
        }
    }

    operator fun plus(other: Matrix): Matrix? {
        return try {
            require(other.numRows == numRows && other.numColumns == numColumns)
            val result = Matrix(numRows, numColumns)
            for (i in 0 until numRows) {
                for (j in 0 until numColumns) {
                    result[i, j] = this[i, j] + other[i, j]
                }
            }
            result
        } catch (ex: Exception) {
            println("The operation cannot be performed.")
            null
        }
    }

    operator fun times(scalar: Double): Matrix {
        val result = Matrix(numRows, numColumns)
        for (i in 0 until numRows) {
            for (j in 0 until numColumns) {
                result[i, j] = scalar * this[i, j]
            }
        }
        return result
    }

    operator fun times(other: Matrix): Matrix? {
        return try {
            require(other.numRows == numColumns)
            val result = Matrix(numRows, other.numColumns)
            for (j in 0 until other.numColumns) {
                val column = other.getColumn(j)
                for (i in 0 until numRows) {
                    result[i, j] = values[i].dot(column)
                }
            }
            result
        } catch (ex: Exception) {
            println("The operation cannot be performed.")
            null
        }
    }

    fun print() {
        for (i in 0 until numRows) {
            for (j in 0 until numColumns) {
                print(String.format("%+-20.2f", values[i][j]))
            }
            println()
        }
    }
}

