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
    val chunks = mutableListOf<List<Instruction>>()
    var curr = mutableListOf<Instruction>()
    for (instr in input) {
        if (instr is Inp) {
            // beginninig of a new chunk
            chunks += curr
            curr = mutableListOf(instr)
        } else {
            curr += instr
        }
    }
    chunks.removeAt(0)
    println(input)
    val blocks = chunks.map { Block(it) }

    println(chunks)

    for (inputNum in 9 downTo 1) {
        val reggy = interpretByBlocks(listOf(blocks[0]), inputNum.toString())
        println(reggy)
    }

    for (inputNum in 99999999999999L downTo 0L) {
        if (inputNum % 10000000L == 0L) {
            println(inputNum)
        }
        val inp = StringBuilder(inputNum.toString())
        if (inp.contains('0')) continue
        val interpreted = interpretByBlocks(blocks, inputNum.toString())
        if (interpreted.isValid()) {
            println(inputNum)
            return
        }
    }
}

fun Map<String, Long>.isValid(): Boolean {
    return this["z"] == 0L
}

fun Registers.isValid(): Boolean {
    return z == 0L
}

data class Block(val l: List<Instruction>) {
    val hashCode = l.hashCode()

    override fun hashCode(): Int {
        return hashCode
    }

    override fun equals(other: Any?): Boolean {
        return l == other
    }
}

var cache = mutableMapOf<Block, Registers>()
fun interpretByBlocks(blocks: List<Block>, input: String): Registers {
    var y: Long = 0L
    var z: Long = 0L
    for ((idx, block) in blocks.withIndex()) {
        val cachedRegg = cache[block] ?: run {
            val res = interpret(block.l, input.drop(idx), mutableMapOf("y" to y, "z" to z))
            val reggs = Registers(res["y"]!!, res["z"]!!)
            cache.put(block, reggs)
            reggs
        }

        y = cachedRegg.y
        z = cachedRegg.z
        if (z != 0L) {
            return Registers(y, z)
        }
    }
    return Registers(y, z)
}

data class Registers(val y: Long, val z: Long)

fun interpret(
    instructions: List<Instruction>,
    inputString: String,
    registers: MutableMap<String, Long>,
): MutableMap<String, Long> {
    val input = StringBuilder(inputString)
    val inputString = null
    fun lookup(value: String): Long {
        val num = value.toLongOrNull()
        if (num != null) return num
        if (value == "z") println(registers[value])
        return registers[value] ?: 0L
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
                registers[inst.register] = input.takeDeleting(1).toLong()
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