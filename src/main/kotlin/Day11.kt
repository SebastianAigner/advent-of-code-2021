@file:OptIn(ExperimentalStdlibApi::class)

package day11

import java.io.File

val input = File("inputs/day11.txt").readLines()
val width = input[0].length
val height = input.size

data class Octopus(var energy: Int)
data class Vec2(val x: Int, val y: Int)

operator fun Map<Vec2, Octopus>.get(x: Int, y: Int): Octopus? {
    return this[Vec2(x, y)]
}

val points = buildMap<Vec2, Octopus> {
    for ((y, line) in input.withIndex()) {
        for ((x, num) in line.withIndex()) {
            put(Vec2(x, y), Octopus(num.digitToInt()))
        }
    }
}

fun main() {
    var repetitions = 0
    var flashes = 0
    repeat(100) {
        flashes += step(points)
        repetitions++
    }
    println(flashes)
    while (true) {
        val thisFlash = step(points)
        repetitions++
        if (thisFlash == 100) {
            break
        }
    }
    println(repetitions)
}

fun step(map: Map<Vec2, Octopus>): Int {
    var flashes = 0
    for ((_, octopus) in map) {
        octopus.energy++
    }
    val flashed = mutableListOf<Vec2>()
    while (map.any { it.value.energy >= 10 }) {
        val toZap = map.filter { it.value.energy >= 10 }
        for ((coord, octopus) in toZap) {
            flashed += coord
            octopus.energy = 0
            flashes++
            val surrounding =
                buildList {
                    for (x in -1..1) {
                        for (y in -1..1) {
                            if (x == 0 && y == 0) continue
                            add(map[coord.x + x, coord.y + y])
                        }
                    }
                }.filterNotNull()
            surrounding.map { it.energy++ }
            break
        }
    }
    for (f in flashed) {
        map[f.x, f.y]?.energy = 0
    }
    return flashes
}

fun Map<Vec2, Octopus>.debugPrint() {
    for (y in 0 until height) {
        for (x in 0 until width) {
            print(this[x, y]?.energy ?: error("$x $y"))
        }
        println()
    }
    println("---")
}