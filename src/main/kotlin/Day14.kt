package day14

import java.io.File

val input = File("inputs/day142.txt").readLines()
val startPoly = input[0]
val rules = input.drop(2).map {
    val (from, to) = it.split(" -> ")
    Rule(from, to)
}
val doubleRules = rules.map {
    DoubleRule.from(it)
}

data class Rule(val from: String, val to: String)
data class DoubleRule(val from: String, val tos: List<String>) {
    companion object {
        fun from(r: Rule): DoubleRule {
            return DoubleRule(r.from, listOf("${r.from[0]}${r.to}", "${r.to}${r.from[1]}"))
        }
    }

    override fun toString(): String {
        return "$from=>$tos"
    }
}

data class PolymerWindow(val identifier: String, val count: Long)


fun countPolymerWindows(list: List<PolymerWindow>): Map<Char, Long> {
    val firstLetter = startPoly[0]
    val map = mutableMapOf<Char, Long>()
    for (polymerWindow in list) {
        val secondLetter = polymerWindow.identifier[1]
        map[secondLetter] = (map[secondLetter] ?: 0) + polymerWindow.count
    }
    map[firstLetter] = (map[firstLetter] ?: 0) + 1
    return map.toMap()
}


fun main() {
    println(doubleRules.count())
    println(startPoly.length)

    val startWindows = startPoly.windowed(2).map { PolymerWindow(it, 1) }
    println(countPolymerWindows(startWindows))
    var currWindow = startWindows
    repeat(40) {
        currWindow = performInsertion(currWindow)
    }
    val mapp = countPolymerWindows(currWindow)
    println(mapp.maxOf { it.value } - mapp.minOf { it.value })
    println(countPolymerWindows(currWindow))
}

fun performInsertion(windows: List<PolymerWindow>): List<PolymerWindow> {
    val newPolyWindows = windows.flatMap { window ->
        val produces = doubleRules.find { it.from == window.identifier }!!.tos
        produces.map { newIdentifier ->
            PolymerWindow(newIdentifier, window.count)
        }
    }
    val uniqueIdentifiers = newPolyWindows.map { it.identifier }.distinct()
    val condensedPolyWindows = uniqueIdentifiers.map { uid ->
        PolymerWindow(uid, newPolyWindows.filter { it.identifier == uid }.sumOf { it.count })
    }
//    println(condensedPolyWindows)
    return condensedPolyWindows
}

fun part1() {
    var poly = startPoly
    repeat(10) {
        poly = poly.insert(rules)
    }
    val common = poly.groupingBy { it }.eachCount()
    println(common.maxOf { it.value } - common.minOf { it.value })
}

fun String.insert(rules: List<Rule>): String {
    val x = this.windowed(2).map { pair ->
        val insertLetter = rules.find { it.from == pair }
        if (insertLetter != null) {
            (pair[0] + insertLetter.to)
        } else pair[0].toString()
    } + this[lastIndex]
    return x.joinToString("")
}