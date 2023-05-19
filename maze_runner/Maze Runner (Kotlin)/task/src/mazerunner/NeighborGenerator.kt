package mazerunner

enum class NeighborDirections(private val x: Int, private val y: Int) {
    UP(0, 1),
    DOWN(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    fun getPosition(cell: Cell): Pair<Int, Int> {
        return Pair(cell.x + x, cell.y + y)
    }
}

class NeighborGenerator {
    operator fun invoke(maze: Maze) {
        for (cell in maze.cells.flatten()) {
            for (direction in NeighborDirections.values()) {
                val (x, y) = direction.getPosition(cell)
                if (maze.isWithin(x, y)) cell.add(Neighbor(maze.cells[y][x]))
            }
        }
    }
}