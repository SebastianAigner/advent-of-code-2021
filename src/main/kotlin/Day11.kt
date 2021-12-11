@file:OptIn(ExperimentalStdlibApi::class)

package day11

import java.io.File

val input = File("inputs/day11.txt").readLines()
val width = input[0].length
val height = input.size

data class Octopus(var energy: Int)
data class Vec2(val x: Int, val y: Int)

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
    val part1 = flashes
    println(part1)
    check(part1 == 1603)
    while (true) {
        val thisFlash = step(points)
        repetitions++
        if (thisFlash == 100) {
            break
        }
    }
    val part2 = repetitions
    check(part2 == 222)
    println(part2)
}

fun step(octopusMap: Map<Vec2, Octopus>): Int {
    var flashes = 0
    for (octopus in octopusMap.values) {
        octopus.energy++
    }
    while (octopusMap.any { it.value.energy >= 10 }) {
        val toZap = octopusMap.filter { it.value.energy >= 10 }
        flashes += toZap.size
        for ((coord, octopus) in toZap) {
            octopus.energy = 0
            coord
                .surrounding
                .mapNotNull { surroundingCoordinate -> octopusMap[surroundingCoordinate] }
                .filter { surroundingOctopus -> surroundingOctopus.energy != 0 }
                .forEach { nonFlashedOctopus -> nonFlashedOctopus.energy++ }
        }
    }
    return flashes
}

val Vec2.surrounding: List<Vec2>
    get() = buildList {
        for (x in -1..1) {
            for (y in -1..1) {
                if (x == 0 && y == 0) continue
                add(Vec2(this@surrounding.x + x, this@surrounding.y + y))
            }
        }
    }

fun Map<Vec2, Octopus>.debugPrint() {
    for (y in 0 until height) {
        for (x in 0 until width) {
            print(this[Vec2(x, y)]?.energy ?: error("$x $y"))
        }
        println()
    }
    println("---")
}