package day09

import java.io.File

val input = File("inputs/day09.txt").readLines()

data class Point(val x: Int, val y: Int, val height: Int)

fun List<Point>.getAt(x: Int, y: Int) = this.find { it.x == x && it.y == y }

val points = mutableListOf<Point>().apply {
    for ((y, line) in input.withIndex()) {
        for ((x, num) in line.withIndex()) {
            this += Point(x, y, num.digitToInt())
        }
    }
}

fun Point.getSurrounding(): List<Point> {
    val above = points.getAt(x, y - 1)
    val below = points.getAt(x, y + 1)
    val left = points.getAt(x - 1, y)
    val right = points.getAt(x + 1, y)
    return listOfNotNull(above, below, left, right)
}

fun part1(): List<Point> {
    val lowPoints = points.filter { point ->
        point.getSurrounding().all { otherPoint ->
            otherPoint.height > point.height
        }
    }
    println(lowPoints.sumOf { it.height + 1 })
    return lowPoints
}

fun main() {
    val lowPoints = part1()
    part2(lowPoints)
}

fun part2(lowPoints: List<Point>) {
    val result = lowPoints
        .asSequence()
        .map { findBasinForLowPoint(it) }
        .distinct()
        .sortedByDescending { it.size }
        .take(3)
        .map { it.size }
        .reduce { a, b -> a * b }
    println(result)
}

fun findBasinForLowPoint(lowPoint: Point): Set<Point> {
    var currentBasin = setOf(lowPoint)
    var newBasin: Set<Point>
    while (true) {
        newBasin = tryExpandBasin(currentBasin)
        if (newBasin.size == currentBasin.size) return newBasin
        currentBasin = newBasin
    }
}

fun tryExpandBasin(currentBasin: Set<Point>): Set<Point> {
    val newBasin = currentBasin.toMutableSet()
    for (point in currentBasin) {
        newBasin.addAll(point.getSurrounding().filter { it.height != 9 })
    }
    return newBasin
}
