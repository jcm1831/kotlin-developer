package mazerunner

import kotlin.random.Random

fun Random.nextOddInt(from: Int, until: Int): Int {
    while (true) {
        val rand = nextInt(from, until)
        if (rand % 2 == 1) return rand else continue
    }
}

class Maze(
    var sizeX: Int = 0,
    var sizeY: Int = 0,
    var entrance: Cell = Cell(),
    var exit: Cell = Cell(),
    var cells: Array<Array<Cell>> = Array(sizeY) { Array(sizeX) { Cell() } }
) {
    fun fromCli() {
        apply {
            println("Enter the size of a maze")
            sizeX = readln().toInt()
            sizeY = sizeX
            cells = Array(sizeY) { Array(sizeX) { Cell() } }
        }
        generateCells()
        generateNeighbors()
        MazeGenerator()(this)
    }

    fun display() {
        for (y in 0 until sizeY) {
            for (x in 0 until sizeX) {
                cells[y][x].print()
                if (cells[y][x].isEscapePassage()) cells[y][x].makePassage()
            }
            println()
        }
        println()
    }

    fun traverse() = MazeTraversal()(this)

    fun makeEntranceAndExit() {
        val randomBoundaryCellWithPassage = {
            cells.flatten()
                 .filter { it.isBoundaryWall() && it.hasNeighborPassage() }
                 .random()
        }
        exit = randomBoundaryCellWithPassage().makePassage()
        entrance = randomBoundaryCellWithPassage().makePassage()
    }

    fun setEntranceAndExit() {
        entrance = cells[entrance.y][entrance.x]
        exit = cells[exit.y][exit.x]
    }

    fun getCell(x: Int, y: Int) = cells[y][x]

    fun randomCell(): Cell {
        val randX = Random.nextOddInt(1, sizeX - 1)
        val randY = Random.nextOddInt(1, sizeY - 1)
        return cells[randY][randX]
    }

    fun isWithin(x: Int, y: Int): Boolean {
        return ((x in 0 until sizeX) && (y in 0 until sizeY))
    }

    fun generateCells() = CellGenerator()(this)
    fun generateNeighbors() = NeighborGenerator()(this)
    fun generateMaze() = MazeGenerator()(this)
}