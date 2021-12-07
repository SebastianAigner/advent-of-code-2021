package day07

import java.io.File
import kotlin.math.abs

val input = File("inputs/day07.txt").readLines()[0].split(",").map { it.toInt() }

fun calculate(costFunction: (start: Int, end: Int) -> Int): Int {
    val min = input.minOrNull()!!
    val max = input.maxOrNull()!!
    val cost = (min..max).map { targetPosition ->
        input.sumOf { crab ->
            costFunction(crab, targetPosition)
        }
    }
    return cost.minOrNull() ?: error("No elements!")
}

fun main() {
    println(calculate { start, end ->
        abs(end - start)
    })
    println(calculate { start, end ->
        val n = abs(end - start)
        n * (n + 1) / 2
    })
}