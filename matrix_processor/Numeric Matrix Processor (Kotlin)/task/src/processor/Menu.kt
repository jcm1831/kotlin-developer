package processor

typealias callback = () -> Unit

class Callbacks(
    private val callbacks: MutableMap<Int, callback> = mutableMapOf()
) {

    fun getInput(): Int {
        print("Your choice: ")
        val option = readln().toInt()
        val action = getCallback(option)
        if (action != null) {
            action()
        } else {
            println("Unknown option!")
        }
        return option
    }

    private fun getCallback(option: Int): callback? {
        return if (callbacks.containsKey(option)) callbacks[option] else null
    }
}

class Menu {
    private val mainCallbacks = Callbacks(
        mutableMapOf(
            0 to ::exit,
            1 to ::add,
            2 to ::multiplyByScalar,
            3 to ::multiplyByMatrix,
            4 to ::transpose,
            5 to ::calculateDeterminant,
            6 to ::inverseMatrix
        )
    )
    private val transposeCallbacks = Callbacks(
        mutableMapOf(
            1 to { printResult(Matrix().transposeAlongMainDiagonal()) },
            2 to { printResult(Matrix().transposeAlongSideDiagonal()) },
            3 to { printResult(Matrix().transposeAlongVerticalLine()) },
            4 to { printResult(Matrix().transposeAlongHorizontalLine()) }
        )
    )

    fun queryInput() {
        do {
            println("1. Add matrices")
            println("2. Multiply matrix by a constant")
            println("3. Multiply matrices")
            println("4. Transpose matrix")
            println("5. Calculate a determinant")
            println("6. Inverse matrix")
            println("0. Exit")
            val option = mainCallbacks.getInput()
            println()
        } while (option != 0)
    }

    private fun exit() {}

    private fun add() {
        val m1 = Matrix(" first ")
        val m2 = Matrix(" second ")
        printResult(m1 + m2)
    }

    private fun multiplyByScalar() {
        val m = Matrix()
        print("Enter constant: ")
        val scalar = readln().toDouble()
        printResult(m * scalar)
    }

    private fun multiplyByMatrix() {
        val m1 = Matrix(" first ")
        val m2 = Matrix(" second ")
        printResult(m1 * m2)
    }

    private fun transpose() {
        println()
        println("1. Main diagonal")
        println("2. Side diagonal")
        println("3. Vertical line")
        println("4. Horizontal line")
        transposeCallbacks.getInput()
    }

    private fun calculateDeterminant() {
        val det = Matrix().calculateDeterminant()
        if (det != null) {
            println("The result is:")
            println(det)
        }
    }

    private fun inverseMatrix() {
        printResult(Matrix().inverseMatrix())
    }

    private fun printResult(matrix: Matrix?) {
        if (matrix != null) {
            println("The result is:")
            matrix.print()
        }
    }
}