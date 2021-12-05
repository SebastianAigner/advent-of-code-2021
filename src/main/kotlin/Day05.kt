package day05

import java.io.File

val input = File("inputs/day05.txt").readLines()

data class Vec2(val x: Int, val y: Int) {
    override fun toString() = "[$x,$y]"
}

data class LineSeg(val from: Vec2, val to: Vec2) {
    override fun toString() = "($from,$to)"

    private fun canonical(): LineSeg {
        if (from.x < to.x) return this // Left to right
        if (from.x == to.x && from.y < to.y) return this // Top to bottom
        return LineSeg(to, from)
    }

    fun createCoords(): List<Vec2> = with(canonical()) {
        if (from.x == to.x) { // vertical line
            return (from.y..to.y).map { Vec2(from.x, it) }
        }
        if (from.y == to.y) { // horizontal line
            return (from.x..to.x).map { Vec2(it, from.y) }
        }
        // diagonal line
        val dx = to.x - from.x
        val dy = to.y - from.y
        val direction = when {
            dy > 0 -> 1
            dy < 0 -> -1
            else -> 0
        }
        // alternatively: val direction = if(dy == 0) 0 else dy / abs(dy)
        return (0..dx).map { delta ->
            Vec2(from.x + delta, from.y + direction * delta)
        }
    }
}

val lineSegments = input.map {
    val (from, to) = it.split(" -> ")
    val (x1, y1) = from.split(",")
    val (x2, y2) = to.split(",")
    LineSeg(Vec2(x1.toInt(), y1.toInt()), Vec2(x2.toInt(), y2.toInt()))
}

fun countIntersections(segments: List<LineSeg>): Int {
    val ventCount = mutableMapOf<Vec2, Int>()
    for (ventLine in segments) {
        for (coord in ventLine.createCoords()) {
            ventCount[coord] = (ventCount[coord] ?: 0) + 1
        }
    }
    return ventCount.filter { it.value >= 2 }.count()
}

fun main() {
    part1()
    part2()
}

fun part1() {
    val filtered = lineSegments.filter { it.from.x == it.to.x || it.from.y == it.to.y }
    println(countIntersections(filtered))
}

fun part2() {
    println(countIntersections(lineSegments))
}


fun printGrid(map: Map<Vec2, Int>) {
    val grid = Array(10) {
        Array(10) {
            '.'
        }
    }
    for ((k, v) in map) {
        grid[k.y][k.x] = v.digitToChar()
    }
    println(grid.joinToString("\n") {
        it.joinToString("")
    })
}