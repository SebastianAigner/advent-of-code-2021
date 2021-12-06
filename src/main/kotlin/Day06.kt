package day06

import java.io.File

val input = File("inputs/day06.txt").readLines()[0].split(",").map { it.toInt() }

fun main() {
    part1()
    part2()
}

class Fish(var timer: Int) {
    fun step(): Fish? {
        if (timer == 0) {
            timer = 6
            return Fish(8)
        }
        timer--
        return null
    }
}

fun part1() {
    val fishes = input.toList().map { Fish(it) }.toMutableList()
    repeat(80) {
        val newFishes = fishes.mapNotNull { it.step() }
        fishes += newFishes
    }
    println(fishes.count())
}


fun part2() {
    val occurences = input.groupingBy { it }.eachCount()
    val startSwarm = occurences.map { (num, count) ->
        FishGeneration(num, count.toLong())
    }
    var curr = startSwarm
    repeat(256) {
        curr = tickSwarm(curr)
    }
    println(curr.sumOf { it.number })
}

fun tickSwarm(fishGens: List<FishGeneration>): List<FishGeneration> {
    val workingCopy = fishGens.toMutableList()
    for (fishGen in workingCopy) {
        fishGen.timer--
    }
    if (!workingCopy.any { it.timer == 6 }) {
        workingCopy += FishGeneration(6, 0)
    }
    if (!workingCopy.any { it.timer == 8 }) {
        workingCopy += FishGeneration(8, 0)
    }
    val genSix = workingCopy.first { it.timer == 6 }
    val genEight = workingCopy.first { it.timer == 8 }
    fishGens.firstOrNull { it.timer == -1 }?.let {
        genEight.number += it.number
        genSix.number += it.number
        it.number = 0
    }
    return workingCopy.filter { it.timer >= 0 }
}

data class FishGeneration(var timer: Int, var number: Long)

