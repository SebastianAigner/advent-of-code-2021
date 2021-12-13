package day13

import java.io.File

data class Vec2(val x: Int, val y: Int)
sealed class FoldLine {
    abstract fun remap(dots: List<Vec2>): List<Vec2>
}

/***
 * 0 .
 * 1 .
 * 2 --
 * 3 ..
 * 4 ##
 */
data class HorFoldLine(val y: Int) : FoldLine() {
    override fun remap(dots: List<Vec2>): List<Vec2> {
        val pointsBelowFold = dots.filter { it.y > y }
        val pointsAboveFold = dots.filter { it.y < y }

        val newPoints = pointsBelowFold.map { p ->
            val distanceFromLine = p.y - y
            val pointAboveLine = p.y - 2 * distanceFromLine
            Vec2(p.x, pointAboveLine)
        }
        return (newPoints union pointsAboveFold).distinct()
    }
}

data class VerFoldLine(val x: Int) : FoldLine() {
    override fun remap(dots: List<Vec2>): List<Vec2> {
        val pointsRightOfFold = dots.filter { it.x > x }
        val pointsLeftOfFold = dots.filter { it.x < x }

        val newPoints = pointsRightOfFold.map { p ->
            val distanceFromLine = p.x - x
            val pointLeftOfLine = p.x - 2 * distanceFromLine
            Vec2(pointLeftOfLine, p.y)
        }
        return (newPoints union pointsLeftOfFold).distinct()
    }
}

val input = File("inputs/day13.txt").readLines()
val dots = input.dropLastWhile { it.isNotBlank() }.filter { it.isNotBlank() }
    .map { val (x, y) = it.split(","); Vec2(x.toInt(), y.toInt()) }
val folds = input.takeLastWhile { it.isNotBlank() }.map {
    val num = it.removePrefix("fold along ")
    val (dir, value) = num.split("=")
    when (dir) {
        "y" -> HorFoldLine(value.toInt())
        "x" -> VerFoldLine(value.toInt())
        else -> error(dir)
    }
}

fun main() {
    println(dots)
    var currDots = dots
    for (fold in folds) {
        currDots = fold.remap(currDots)
        println(currDots.count())
        val resStr = currDots.debugPrint()
        println(resStr)
    }
}

fun List<Vec2>.debugPrint() = buildString {
    val width = this@debugPrint.maxOf { it.x }
    val height = this@debugPrint.maxOf { it.y }
    for (y in 0..height) {
        for (x in 0..width) {
            if (this@debugPrint.any { it.x == x && it.y == y }) append('#') else append('.')
        }
        appendLine()
    }
}