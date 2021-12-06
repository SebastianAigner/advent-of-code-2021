package day06

import java.io.File

val input = File("inputs/day06.txt").readLines()[0].split(",").map { it.toInt() }

fun main() {
    // Part 1
    simulateIndividualFishes(80)
    // Part 1 by generation
    simulateGenerations(80)
    // Part 1 by map
    simulateFishMap(80)
    // Part 2 by generation
    simulateGenerations(256)
    // Part 2 by map
    simulateFishMap(256)
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

fun simulateIndividualFishes(generations: Int) {
    val fishes = input.map { Fish(it) }.toMutableList()
    repeat(generations) {
        val newFishes = fishes.mapNotNull { it.step() }
        fishes += newFishes
    }
    println(fishes.count())
}

fun simulateFishMap(generations: Int) {
    val occurrences = input
        .groupingBy { it }
        .eachCount()
        .mapValues { (_, v) -> v.toLong() }
    var curr = occurrences
    repeat(generations) {
        curr = tickMap(curr)
    }
    println(curr.values.sum())
}

fun tickMap(fishGens: Map<Int, Long>): Map<Int, Long> {
    val newMap = fishGens.mapKeys { (key, _) -> key - 1 }.toMutableMap()
    newMap[8] = newMap.getOrDefault(-1, 0)
    newMap[6] = newMap.getOrDefault(6, 0) + newMap.getOrDefault(-1, 0)
    newMap.remove(-1)
    return newMap
}

data class FishGeneration(var timer: Int, var number: Long)


fun simulateGenerations(generations: Int) {
    val startSwarm = input
        .groupingBy { it }
        .eachCount()
        .map { (num, count) ->
            FishGeneration(num, count.toLong())
        }
    var curr = startSwarm
    repeat(generations) {
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

