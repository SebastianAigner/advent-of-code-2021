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
}