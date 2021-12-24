package day24

import day18.deletePrefix
import java.io.File

val input = File("inputs/day24.txt").readLines().map {
    val list = it.split(" ")
    if (list.size == 2) {
        // inp
        return@map Inp(list[1])
    } else {
        val inst = list[0]
        val targetRegister = list[1]
        val value = list[2]
        when (inst) {
            "add" -> Add(targetRegister, value)
            "mul" -> Mul(targetRegister, value)
            "div" -> Div(targetRegister, value)
            "mod" -> Mod(targetRegister, value)
            "eql" -> Eql(targetRegister, value)
            else -> error("Unknown $inst")
        }
    }
}

sealed class Instruction
data class Inp(val register: String) : Instruction()
data class Add(val targetRegister: String, val value: String) : Instruction()
data class Mul(val targetRegister: String, val value: String) : Instruction()
data class Div(val targetRegister: String, val value: String) : Instruction()
data class Mod(val targetRegister: String, val value: String) : Instruction()
data class Eql(val targetRegister: String, val value: String) : Instruction()

fun main() {
    println(input)
    for (inputNum in 99999999999999L downTo 0L) {
        if (inputNum % 100000L == 0L) {
            println(inputNum)
        }
        val inp = StringBuilder(inputNum.toString())
        if (inp.contains('0')) continue
        val interpreted = interpret(input, inp)
        if (interpreted.isValid()) {
            println(inputNum)
            return
        }
    }
}

fun Map<String, Int>.isValid(): Boolean {
    return this["z"] == 0
}

fun interpret(instructions: List<Instruction>, input: StringBuilder): Map<String, Int> {
    val registers = mutableMapOf<String, Int>()
    fun lookup(value: String): Int {
        val num = value.toIntOrNull()
        if (num != null) return num
        return registers[value] ?: 0
    }
    for (inst in instructions) {
        when (inst) {
            is Add -> {
                val curr = registers[inst.targetRegister] ?: 0
                registers[inst.targetRegister] = curr + lookup(inst.value)
            }
            is Div -> {
                val curr = registers[inst.targetRegister] ?: 0
                registers[inst.targetRegister] = curr / lookup(inst.value) // TODO: Truncating division?
            }
            is Eql -> {
                registers[inst.targetRegister] = if (registers[inst.targetRegister] == lookup(inst.value)) 1 else 0
            }
            is Inp -> {
                registers[inst.register] = input.takeDeleting(1).toInt()
            }
            is Mod -> {
                val curr = registers[inst.targetRegister] ?: 0
                registers[inst.targetRegister] = curr % lookup(inst.value) // TODO: mod instead of rem?
            }
            is Mul -> {
                val curr = registers[inst.targetRegister] ?: 0
                registers[inst.targetRegister] = curr * lookup(inst.value)
            }
        }
    }
    return registers
}

fun StringBuilder.takeDeleting(len: Int): String {
    val res = this.take(len)
    this.deletePrefix(len)
    return res.toString()
}