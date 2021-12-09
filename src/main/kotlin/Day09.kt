package day09

import java.io.File

val input = File("inputs/day09.txt").readLines()

data class Point(val x: Int, val y: Int, val height: Int)

fun List<Point>.getAt(x: Int, y: Int) = this.find { it.x == x && it.y == y }

val list = mutableListOf<Point>().apply {
    for ((y, line) in input.withIndex()) {
        for ((x, num) in line.withIndex()) {
            this += Point(x, y, num.digitToInt())
        }
    }
}

fun part1(): List<Point> {
    println(list)
    val lowPoints = mutableListOf<Point>()
    for (point in list) {
        // above
        val above = list.getAt(point.x, point.y - 1)
        val below = list.getAt(point.x, point.y + 1)
        val left = list.getAt(point.x - 1, point.y)
        val right = list.getAt(point.x + 1, point.y)
        val isLowPoint = listOfNotNull(above, below, left, right).all { otherPoint ->
            otherPoint.height > point.height
        }
        if (isLowPoint) lowPoints += point
    }
    println(lowPoints.sumOf { it.height + 1 })
    return lowPoints
}

fun main() {
    val lowPoints = part1()
    part2(lowPoints)
}

fun part2(lowPoints: List<Point>) {
    // every low point has a basin
    // (but i suspect there might be duplicates)
    val (a, b, c) = lowPoints.map { findBasinForLowPoint(it, list) }.distinct().sortedByDescending { it.size }.take(3)
        .map { it.size }
    println(a * b * c)
}

fun findBasinForLowPoint(lowPoint: Point, map: List<Point>): Set<Point> {
    var currentBasin = setOf(lowPoint)
    var newBasin: Set<Point> = currentBasin
    while (true) {
        newBasin = tryExpandBasin(currentBasin, map)
        if (newBasin.size == currentBasin.size) return newBasin
        currentBasin = newBasin
    }
}

fun tryExpandBasin(currentBasin: Set<Point>, map: List<Point>): Set<Point> {
    val newBasin = currentBasin.toMutableSet()
    for (point in currentBasin) {
        val above = list.getAt(point.x, point.y - 1)
        val below = list.getAt(point.x, point.y + 1)
        val left = list.getAt(point.x - 1, point.y)
        val right = list.getAt(point.x + 1, point.y)
        val newAdditions = listOfNotNull(above, below, left, right).filter { it.height != 9 }
        newBasin.addAll(newAdditions)
    }
    return newBasin
}
