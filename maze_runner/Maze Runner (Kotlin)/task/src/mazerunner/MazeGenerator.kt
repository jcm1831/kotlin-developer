package mazerunner

enum class FrontierDirections(private val x: Int, private val y: Int) {
    UP(0, 2),
    DOWN(0, -2),
    LEFT(-2, 0),
    RIGHT(2,0);

    fun getFrontierCellPosition(cell: Cell): Pair<Int, Int> {
        return Pair(cell.x + x, cell.y + y)
    }

    fun getConnectingCellPosition(cell: Cell): Pair<Int, Int> {
        return Pair(cell.x + x / 2, cell.y + y / 2)
    }
}

data class FrontierCell(val cell: Cell) {
    private val connectivity = arrayListOf<Cell>()

    fun makePassage() {
        cell.makePassage()
        connectivity.random().makePassage()
    }

    fun addConnectedCell(cell: Cell) = connectivity.add(cell)
}

class MazeGenerator {
    private val frontierCells = arrayListOf<FrontierCell>()

    operator fun invoke(maze: Maze) {
        // get random cell (aka seed of maze) and set its state to passage
        val seed = maze.randomCell()
        seed.makePassage()
        // compute frontier cells
        determineFrontierCells(seed, maze)
        // iterate over all frontier cells
        while (frontierCells.isNotEmpty()) {
            // pick random frontier cell
            val randomFrontierCell = frontierCells.random()
            // set cell between cell and frontier cell to passage
            randomFrontierCell.makePassage()
            // get frontier cells of frontier cell
            determineFrontierCells(randomFrontierCell, maze)
            // remove current frontier cell from list
            frontierCells.remove(randomFrontierCell)
        }
        // generate entrance and exit point
        maze.makeEntranceAndExit()
    }

    private fun determineFrontierCells(anchor: Cell, maze: Maze) {
        for (direction in FrontierDirections.values()) {
            val (xfc, yfc) = direction.getFrontierCellPosition(anchor)
            if (maze.isWithin(xfc, yfc) && maze.getCell(xfc, yfc).isInteriorWall()) {
                val (xcc, ycc) = direction.getConnectingCellPosition(anchor)
                // check is cell is already contained in frontier cells
                val refCell = frontierCells.find { it.cell == maze.getCell(xfc, yfc) }
                if (refCell != null) {
                    refCell.addConnectedCell(maze.getCell(xcc, ycc))
                } else {
                    val frontierCell = FrontierCell(maze.getCell(xfc, yfc))
                    frontierCell.addConnectedCell(maze.getCell(xcc, ycc))
                    frontierCells.add(frontierCell)
                }
            }
        }
    }

    private fun determineFrontierCells(anchor: FrontierCell, maze: Maze) {
        determineFrontierCells(anchor.cell, maze)
    }
}