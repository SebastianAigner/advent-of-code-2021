package day01

import java.io.File

val input = File("inputs/day01.txt").readLines().map { it.toInt() }

fun main() {
    println(
        input
            .windowed(2)
            .count { (a, b) -> b > a }
    )
    println(
        input
            .windowed(3) { it.sum() }
            .windowed(2)
            .count { (a, b) -> b > a }
    )
    // B + C + D > A + B + C can be simplified: substract (B + C) from both sides:
    // D > A
    println(
        input
            .windowed(4) { it[3] > it[0]}
            .count { it }
    )
}