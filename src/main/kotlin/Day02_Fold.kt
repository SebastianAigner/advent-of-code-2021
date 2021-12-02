package day02.fold

import java.io.File

sealed class Instruction(val value: Int) {
    abstract fun execute(state: SimpleSubmarineState): SimpleSubmarineState
    abstract fun execute(state: AimedSubmarineState): AimedSubmarineState
    companion object {
        fun fromText(str: String): Instruction {
            val (name, valueText) = str.split(" ")
            val value = valueText.toInt()
            return when(name) {
                "forward" -> Forward(value)
                "down" -> Down(value)
                "up" -> Up(value)
                else -> error("Unknown type of instruction")
            }
        }
    }
}

class Forward(value: Int): Instruction(value) {
    override fun execute(state: SimpleSubmarineState): SimpleSubmarineState = state.copy(hor = state.hor + value)
    override fun execute(state: AimedSubmarineState) = state.copy(hor = state.hor + value, dep = state.dep + state.aim * value)
}

class Down(value: Int): Instruction(value) {
    override fun execute(state: SimpleSubmarineState) = state.copy(dep = state.dep + value)
    override fun execute(state: AimedSubmarineState) = state.copy(aim = state.aim + value)
}

class Up(value: Int): Instruction(value) {
    override fun execute(state: SimpleSubmarineState) = state.copy(dep = state.dep - value)
    override fun execute(state: AimedSubmarineState) = state.copy(aim = state.aim - value)
}

data class SimpleSubmarineState(val hor: Int, val dep: Int)
data class AimedSubmarineState(val hor: Int, val dep: Int, val aim: Int)

val input = File("inputs/day02.txt")
    .readLines()
    .map { Instruction.fromText(it) }

fun part1() {
    val finalState = input.fold(SimpleSubmarineState(0, 0)) { acc, inst ->
        inst.execute(acc)
    }
    println(finalState.hor * finalState.dep)
}

fun part2() {
    val finalState = input.fold(AimedSubmarineState(0, 0, 0)) { acc, inst ->
        inst.execute(acc)
    }
    println(finalState.hor * finalState.dep)
}

fun main() {
    part1()
    part2()
}