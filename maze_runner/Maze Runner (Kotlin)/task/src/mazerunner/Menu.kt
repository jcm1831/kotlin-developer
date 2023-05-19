package mazerunner

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val mazeAdapter: JsonAdapter<Maze> = moshi.adapter(Maze::class.java)

typealias action = () -> Unit

class Menu {
    private val callbacks = mapOf(
        0 to ::exit,
        1 to ::generate,
        2 to ::load,
        3 to ::save,
        4 to ::display,
        5 to ::findEscape,
    )
    private lateinit var maze: Maze

    fun inputAction() {
        do {
            printOptions()
            val numOption = readln().toInt()
            val action = getAction(numOption)
            if (action != null) {
                action.invoke()
            } else {
                println("Incorrect option. Please try again")
            }
        } while (numOption != 0)
    }

    private fun printOptions() {
        println("=== Menu ===")
        println("1. Generate a new maze")
        println("2. Load a maze")
        if (::maze.isInitialized) {
            println("3. Save the maze")
            println("4. Display the maze")
            println("5. Find the escape")
        }
        println("0. Exit")
    }

    private fun getAction(number: Int): action? {
        return if (callbacks.containsKey(number)) callbacks[number] else null
    }

    private fun exit() = println("Bye!")
    private fun generate() {
        maze = Maze()
        maze.fromCli()
        maze.display()
    }

    private fun load() {
        print("Enter filepath: ")
        val filepath = readln()
        if (File(filepath).exists()) {
            val jsonString = File(filepath).readText()
            if (jsonString.isNotEmpty()) {
                maze = mazeAdapter.fromJson(File(filepath).readText())!!
                maze.setEntranceAndExit()
                maze.generateNeighbors()
            } else {
                println("Cannot load the maze. It has an invalid format")
            }
        } else {
            println("The file $filepath does not exist")
        }
    }

    private fun save() {
        print("Enter filepath: ")
        val filepath = readln()
        File(filepath).writeText(mazeAdapter.toJson(maze))
    }

    private fun display() = maze.display()

    private fun findEscape() {
        maze.traverse()
        maze.display()
    }
}