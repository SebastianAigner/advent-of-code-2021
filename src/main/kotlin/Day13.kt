package day13

import java.io.File

data class Vec2(val x: Int, val y: Int)

val input = File("inputs/day13.txt").readLines()

val dots = input
    .takeWhile { it.isNotBlank() }
    .map {
        val (x, y) = it.split(",")
        Vec2(x.toInt(), y.toInt())
    }

val folds = input
    .takeLastWhile { it.isNotBlank() }
    .map {
        val (dir, value) = it.removePrefix("fold along ").split("=")
        when (dir) {
            "y" -> HorFoldLine(value.toInt())
            "x" -> VerFoldLine(value.toInt())
            else -> error(dir)
        }
    }

fun main() {
    println(folds[0].remap(dots).count())
    val foldedPaper = folds.fold(dots) { paper, foldLine ->
        foldLine.remap(paper)
    }
    println(foldedPaper.visualize())
}

fun List<Vec2>.visualize() = buildString {
    val width = this@visualize.maxOf { it.x }
    val height = this@visualize.maxOf { it.y }
    for (y in 0..height) {
        for (x in 0..width) {
            if (this@visualize.any { it.x == x && it.y == y }) append('#') else append(' ')
        }
        appendLine()
    }
}

sealed class FoldLine {
    abstract fun remap(dots: List<Vec2>): List<Vec2>
}

data class HorFoldLine(val y: Int) : FoldLine() {
    override fun remap(dots: List<Vec2>): List<Vec2> {
        return dots.map { p ->
            if (p.y < y) return@map p
            Vec2(p.x, p.y - 2 * (p.y - y))
        }.distinct()
    }
}

data class VerFoldLine(val x: Int) : FoldLine() {
    override fun remap(dots: List<Vec2>): List<Vec2> {
        return dots.map { p ->
            if (p.x < x) return@map p
            Vec2(p.x - 2 * (p.x - x), p.y)
        }.distinct()
    }
}

// A more verbose implementation (that gives things names) would look like this:
//data class VerFoldLine(val x: Int) : FoldLine() {
//    override fun remap(dots: List<Vec2>): List<Vec2> {
//        val pointsRightOfFold = dots.filter { it.x > x }
//        val pointsLeftOfFold = dots.filter { it.x < x }
//
//        val newPoints = pointsRightOfFold.map { p ->
//            val distanceFromLine = p.x - x
//            val pointLeftOfLine = p.x - 2 * distanceFromLine
//            Vec2(pointLeftOfLine, p.y)
//        }
//        return (newPoints union pointsLeftOfFold).distinct()
//    }
//}