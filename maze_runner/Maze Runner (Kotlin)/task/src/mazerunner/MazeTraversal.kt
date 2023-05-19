package mazerunner

class MazeTraversal {
    private val stack = ArrayDeque<Cell>()
    private var currentCell = Cell()

    operator fun invoke(maze: Maze) {
        stack.addFirst(maze.entrance.makeVisited())
        currentCell = maze.entrance
        depthFirstSearch(maze)
        stack.forEach { it.makeEscapePassage() }
    }

    private fun depthFirstSearch(maze: Maze) {
        // reduction step
        val adjacencyList = currentCell.neighbors.filter {
            it.isPassage() && it.hasNotBeenVisited()
        }.toMutableList()
        while (adjacencyList.isNotEmpty()) {
            val randomNeighbor = adjacencyList.random()
            stack.addFirst(randomNeighbor.makeVisited())
            adjacencyList.remove(randomNeighbor)
            currentCell = stack.first()
            depthFirstSearch(maze)
        }
        // trivial base case
        if (currentCell == maze.exit) {
            return
        } else {
            stack.removeFirst()
            if (stack.isNotEmpty()) currentCell = stack.first()
        }
    }
}