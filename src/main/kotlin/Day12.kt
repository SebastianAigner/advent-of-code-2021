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

    findAllPaths(listOf("start"))
    println(allPaths.count())

    for (smallCave in nodeNames.filter { it.first().isLowerCase() && it !in listOf("start", "end") }) {
        println("Working with $smallCave as big")
        findAllPaths2(listOf("start"), withBigCave = smallCave)
    }
    println(allPaths2.distinct().count())
}

val allPaths = mutableListOf<List<String>>()

fun findAllPaths(pathSoFar: List<String>) {
    val last = pathSoFar.last()
    if (last == "end") {
        allPaths += pathSoFar
        println(pathSoFar)
        return
    }
    val adjacents = matrix.adjacent(last)
    val candidates = adjacents.filter { it.first().isUpperCase() || it !in pathSoFar }
    for (candidate in candidates) {
        findAllPaths(pathSoFar + candidate)
    }
}

val allPaths2 = mutableListOf<List<String>>()

fun findAllPaths2(pathSoFar: List<String>, withBigCave: String) {
    println(pathSoFar)
    val last = pathSoFar.last()
    if (last == "end") {
        allPaths2 += pathSoFar
        println(pathSoFar)
        return
    }
    val adjacents = matrix.adjacent(last)
    val candidates = adjacents.filter {
        it.first()
            .isUpperCase() || it !in pathSoFar || (it == withBigCave && pathSoFar.count { it == withBigCave } <= 1)
    }.filter { it != "start" }
    for (candidate in candidates) {
        findAllPaths2(pathSoFar + candidate, withBigCave)
    }
}

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