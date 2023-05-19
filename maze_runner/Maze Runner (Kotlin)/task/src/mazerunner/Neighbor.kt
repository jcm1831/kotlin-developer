package mazerunner

class Neighbor(private val cell : Cell = Cell()) {
    fun makeVisited() = cell.makeVisited()
    fun hasNotBeenVisited() = !cell.hasBeenVisited()
    fun isPassage() = cell.isPassage()
}