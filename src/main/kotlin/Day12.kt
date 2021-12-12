@file:OptIn(ExperimentalStdlibApi::class)

package day12

import java.io.File

val input = File("inputs/day12.txt").readLines().map { val (from, to) = it.split("-"); from to to }
val nodeNames = input.flatMap { it.toList() }.distinct().sorted()
val nodeNum = nodeNames.count()

fun String.matrixIndex(): Int {
    return nodeNames.indexOf(this)
}

val matrix = MutableList(nodeNum) {
    MutableList(nodeNum) {
        false
    }
}

fun main() {
    for (pair in input) {
        matrix[pair.first.matrixIndex()][pair.second.matrixIndex()] = true
        matrix[pair.second.matrixIndex()][pair.first.matrixIndex()] = true
    }
    matrix.debug()

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
    val adjacents = matrix.adjacent(last)
    val candidates = adjacents.filter {
        it.isLargeCave || it !in (pathSoFar + "start") || (it == withBigCave && pathSoFar.count { it == withBigCave } <= 1)
    }
    for (candidate in candidates) {
        findAllPaths(pathSoFar + candidate, withBigCave, targetCollection)
    }
}

val String.isLargeCave get() = first().isUpperCase()

fun List<List<Boolean>>.adjacent(name: String): List<String> {
    val booles = this[name.matrixIndex()]
    return booles.mapIndexedNotNull { index, b -> if (b) nodeNames[index] else null }
}

fun List<List<Boolean>>.debug() {
    println("      " + nodeNames.joinToString())
    for ((idx, line) in this.withIndex()) {
        println(nodeNames[idx].padStart(5) + " " + line.joinToString() { if (it) "1" else "0" })
    }
}