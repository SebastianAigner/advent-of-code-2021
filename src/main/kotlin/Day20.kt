package day20

import java.io.File

val input = File("inputs/day20.txt").readLines()
val enhancement = input[0]
val inputImage = input.drop(2)

data class Vec2(val x: Int, val y: Int) {
    fun get3by3(): List<Vec2> {
        val l = mutableListOf<Vec2>()
        for (y in (y - 1)..(y + 1)) {
            for (x in (x - 1)..(x + 1)) {
                l += Vec2(x, y)
            }
        }
        return l
    }
}

data class Pixel(val v: Vec2, val lit: Boolean) {

}

data class BoundingRect(val minX: Int, val minY: Int, val maxX: Int, val maxY: Int)

data class InputImage(val pixels: Map<Vec2, Pixel>) {
    fun getBoundingRect(): BoundingRect {
        val minX = this.pixels.keys.minOf { it.x }
        val minY = this.pixels.keys.minOf { it.y }
        val maxX = this.pixels.keys.maxOf { it.x }
        val maxY = this.pixels.keys.maxOf { it.y }

        return BoundingRect(minX, minY, maxX, maxY)
    }

    fun countLit(): Int {
        return pixels.count {
            it.value.lit
        }
    }

    fun enhance(isBackgroundLit: Boolean): InputImage {
        val newImage = pixels.toMutableMap()
        val (minX, minY, maxX, maxY) = getBoundingRect()
        for (pixelY in minY - 1..maxY + 1) {
            for (pixelX in minX - 1..maxX + 1) {
                val pixel = Vec2(pixelX, pixelY)
                val binNum = StringBuilder()
                val surrounding = pixel.get3by3()
                for (kernelPixel in surrounding) {
                    val sPix = pixels[kernelPixel]
                    if (sPix != null && sPix.lit || sPix == null && isBackgroundLit) {
                        binNum.append("1")
                    } else {
                        binNum.append("0")
                    }
                }
                val index = binNum.toString().toInt(2)
                val newPixel = enhancement[index]
//                newImage.remove(pixel)
                if (newPixel == '#') {
                    newImage.put(pixel, Pixel(pixel, true))
                } else {
                    newImage.put(pixel, Pixel(pixel, false))
                }
            }
        }
        return InputImage(newImage)
    }
}

fun main() {
    val m = mutableMapOf<Vec2, Pixel>()
    for ((y, line) in inputImage.withIndex()) {
        for ((x, char) in line.withIndex()) {
            m[Vec2(x, y)] = Pixel(Vec2(x, y), char == '#')
        }
    }
    var curr = InputImage(m)
    repeat(50) {
        curr = curr.enhance(it % 2 == 1)
//        curr.debugPrint()
    }
    curr.debugPrint()
    println(curr.countLit()) // not 10091. not 5354. not 4960. not 5464.
    // should be 5326
}

fun InputImage.debugPrint() {
    val minX = this.pixels.keys.minOf { it.x }
    val minY = this.pixels.keys.minOf { it.y }
    val maxX = this.pixels.keys.maxOf { it.x }
    val maxY = this.pixels.keys.maxOf { it.y }
    for (y in minY..maxY) {
        for (x in minX..maxX) {
            val poxel = pixels[Vec2(x, y)]
            val char = if (poxel != null && poxel.lit) '#' else '.'
            print(char)
        }
        println()
    }
}