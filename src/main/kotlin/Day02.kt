package day02


import java.io.File

data class Instruction(val keyword: String, val value: Int)

val input = File("inputs/day02.txt")
    .readLines()
    .map {
        val (name, value) = it.split(" ")
        Instruction(name, value.toInt())
    }

fun part1() {
    var hor = 0
    var dep = 0
    for (inst in input) {
        when (inst.keyword) {
            "forward" -> hor += inst.value
            "down" -> dep += inst.value
            "up" -> dep -= inst.value
        }
    }
    println(dep * hor)
}

fun part2() {
    var hor = 0
    var dep = 0
    var aim = 0
    for (inst in input) {
        when (inst.keyword) {
            "forward" -> {
                hor += inst.value
                dep += aim * inst.value
            }
            "down" -> aim += inst.value
            "up" -> aim -= inst.value
        }
    }
    println(dep * hor)
}

fun main() {
    part1()
    part2()
}