package mazerunner

class CellGenerator {
    operator fun invoke(maze: Maze) {
        maze.cells.forEachIndexed { y, cellRow ->
            cellRow.forEachIndexed { x, cell ->
                cell.init(x, y, maze.sizeX, maze.sizeY)
            }
        }
    }
}