@file:OptIn(ExperimentalStdlibApi::class)

package day12

import java.io.File

val input = File("inputs/day12.txt").readLines().map { val (from, to) = it.split("-"); from to to }
val nodeNames = input.flatMap { it.toList() }.distinct().sorted()
val transitions: Map<String, List<String>> = buildMap<String, MutableList<String>> {
    for ((from, to) in input) {
        this.getOrPut(from) { mutableListOf() }.add(to)
        this.getOrPut(to) { mutableListOf() }.add(from)
    }
}

fun main() {
    val targetSet = mutableSetOf<List<String>>()

    findAllPaths(listOf("start"), withBigCave = null, targetSet)
    val part1 = targetSet.count()
    println(part1)
    check(part1 == 5252)

    targetSet.clear()

    for (smallCave in nodeNames.filter { !it.isLargeCave && it !in listOf("start", "end") }) {
        findAllPaths(listOf("start"), withBigCave = smallCave, targetSet)
    }
    val part2 = targetSet.count()
    println(part2)
    check(part2 == 147784)
}

fun findAllPaths(pathSoFar: List<String>, withBigCave: String?, targetCollection: MutableSet<List<String>>) {
    val last = pathSoFar.last()
    if (last == "end") {
        targetCollection += pathSoFar
        return
    }
    val adjacents = transitions.getValue(last)
    val candidates = adjacents.filter {
        it.isLargeCave || it !in (pathSoFar + "start") || (it == withBigCave && pathSoFar.count { it == withBigCave } <= 1)
    }
    for (candidate in candidates) {
        findAllPaths(pathSoFar + candidate, withBigCave, targetCollection)
    }
}

val String.isLargeCave get() = first().isUpperCase()