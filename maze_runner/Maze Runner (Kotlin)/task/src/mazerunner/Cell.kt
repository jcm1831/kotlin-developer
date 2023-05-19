package mazerunner

enum class CellStates(private val color: String) {
    BOUNDARY_WALL("\u2588\u2588"),
    INTERIOR_WALL("\u2588\u2588"),
    PASSAGE("  "),
    ESCAPE_PASSAGE("//");
    override fun toString() = color
}

class Cell {
    var x: Int = 0
    var y: Int = 0
    private var visited = false
    private var state = CellStates.INTERIOR_WALL
    val neighbors = mutableListOf<Neighbor>()

    fun init(xx: Int, yy: Int, sizeX: Int, sizeY: Int) {
        x = xx
        y = yy
        val isBoundary = { z: Int, size: Int -> z == 0 || z == (size - 1) }
        if (isBoundary(x, sizeX) || isBoundary(y, sizeY)) {
            state = CellStates.BOUNDARY_WALL
        }
    }

    fun print() = print(state)

    fun makePassage() : Cell {
        state = CellStates.PASSAGE
        return this
    }
    fun makeVisited() : Cell {
        visited = true
        return this
    }
    fun makeEscapePassage() {
        state = CellStates.ESCAPE_PASSAGE
    }
    fun hasBeenVisited() = visited

    fun add(neighbor: Neighbor) = neighbors.add(neighbor)

    fun hasNeighborPassage() = neighbors.any { it.isPassage() }

    fun isBoundaryWall() = state == CellStates.BOUNDARY_WALL
    fun isInteriorWall() = state == CellStates.INTERIOR_WALL
    fun isPassage() = state == CellStates.PASSAGE
    fun isEscapePassage() = state == CellStates.ESCAPE_PASSAGE
}