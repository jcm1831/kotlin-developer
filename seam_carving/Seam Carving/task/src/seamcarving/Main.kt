package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.min
import kotlin.math.sqrt

fun <T> ArrayList<ArrayList<T>>.transpose(): ArrayList<ArrayList<T>> {
    val (rows, columns) = Pair(this.size, this.first().size)
    val result = ArrayList<ArrayList<T>>()
    repeat(columns) { result.add(ArrayList()) }
    for (i in 0 until rows) {
        val row = this[i]
        for (j in 0 until columns) {
            result[j].add(row[j])
        }
    }
    return result
}

fun squaredGradient(pixel1: Color, pixel2: Color): Int {
    val deltaRed = pixel2.red - pixel1.red
    val deltaGreen = pixel2.green - pixel1.green
    val deltaBlue = pixel2.blue - pixel1.blue
    return deltaRed * deltaRed + deltaGreen * deltaGreen + deltaBlue * deltaBlue
}

fun energy(squaredGradX: Int, squaredGradY: Int): Double {
    return sqrt(squaredGradX.toDouble() + squaredGradY.toDouble())
}

fun effectivePixel(x: Int, y: Int, image: BufferedImage): Pair<Int, Int> {
    val effectiveValue: (Int, Int) -> Int = { value: Int, size: Int ->
        when (value) {
            0 -> 1
            size -> value - 1
            else -> value
        }
    }
    return Pair(
        effectiveValue(x, image.width - 1),
        effectiveValue(y, image.height - 1)
    )
}

fun calculateEnergies(image: BufferedImage): ArrayList<ArrayList<Double>> {
    val energies = ArrayList<ArrayList<Double>>()
    repeat(image.width) { energies.add(ArrayList()) }
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            val (xx, yy) = effectivePixel(x, y, image)

            val squaredGradX = squaredGradient(
                Color(image.getRGB(xx - 1, y)),
                Color(image.getRGB(xx + 1, y))
            )
            val squaredGradY = squaredGradient(
                Color(image.getRGB(x, yy - 1)),
                Color(image.getRGB(x, yy + 1))
            )

            energies[x].add(energy(squaredGradX, squaredGradY))
        }
    }
    return energies
}

fun accumulateEnergies(energies: ArrayList<ArrayList<Double>>) {
    val (width, height) = Pair(energies.size, energies.first().size)
    for (y in 1 until height) {
        for (x in 0 until width) {
            var minimum = Double.MAX_VALUE
            for (xShift in -1..+1) {
                val xx = x + xShift
                if (xx in 0 until width) {
                    minimum = min(minimum, energies[xx][y - 1])
                }
            }
            energies[x][y] = energies[x][y] + minimum
        }
    }
}

fun getSeam(energies: ArrayList<ArrayList<Double>>): Array<Int> {

    val energiesTrans = energies.transpose()
    val first = energiesTrans.last().indexOf(energiesTrans.last().minOrNull())
    val indices = arrayListOf(first)

    var current = first
    var next = 0
    val (width, height) = Pair(energies.size, energies.first().size)
    for (y in height - 1 downTo 1) {
        var minimum = Double.MAX_VALUE
        for (xShift in -1..+1) {
            val xx = current + xShift
            if (xx in 0 until width) {
                if (energies[xx][y - 1] < minimum) {
                    minimum = energies[xx][y - 1]
                    next = xx
                }
            }
        }
        current = next
        indices.add(next)
    }

    indices.reverse()
    return indices.toTypedArray()
}

fun removeSeam(colorMap: ArrayList<ArrayList<Int>>, seam: Array<Int>) {
    seam.forEachIndexed { index, i -> colorMap[index].removeAt(i) }
}

fun updateImage(colorMap: ArrayList<ArrayList<Int>>): BufferedImage {
    val image = BufferedImage(
        colorMap.size, colorMap.first().size,
        BufferedImage.TYPE_3BYTE_BGR
    )
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            image.setRGB(x, y, colorMap[x][y])
        }
    }
    return image
}

fun main(args: Array<String>) {
    // parse cli parameters
    val inputFile = args[1]
    val outputFile = args[3]
    val numWidth = args[5].toInt()
    val numHeight = args[7].toInt()

    // load image
    var image: BufferedImage = ImageIO.read(File(inputFile))

    // generate auxiliary color map for removing seams
    var colorMap = ArrayList<ArrayList<Int>>()
    for (x in 0 until image.width) {
        val column = ArrayList<Int>()
        for (y in 0 until image.height) {
            column.add(image.getRGB(x, y))
        }
        colorMap.add(column)
    }

    // remove vertical seams
    repeat(numWidth) {
        colorMap = colorMap.transpose()
        // calculate energies
        val energies = calculateEnergies(image)
        accumulateEnergies(energies)
        // find and remove vertical seam
        removeSeam(colorMap, getSeam(energies))
        colorMap = colorMap.transpose()
        image = updateImage(colorMap)
    }

    // remove horizontal seams
    repeat(numHeight) {
        // calculate energies
        val energies = calculateEnergies(image).transpose()
        accumulateEnergies(energies)
        // find & remove horizontal seam
        removeSeam(colorMap, getSeam(energies))
        image = updateImage(colorMap)
    }

    // write image to file
    ImageIO.write(image, "png", File(outputFile))
}
