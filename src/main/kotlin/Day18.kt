package day18

import java.io.File

val input = File("inputs/day18.txt").readLines()

sealed class SnailfishNumber {
    fun add(other: SnailfishNumber): PairNumber {
        return PairNumber(this, other)
    }

    abstract fun magnitude(): Long
    abstract fun deepCopy(): SnailfishNumber
}

data class PairNumber(var left: SnailfishNumber, var right: SnailfishNumber) : SnailfishNumber() {
    override fun magnitude(): Long {
        return left.magnitude() * 3 + right.magnitude() * 2
    }

    override fun deepCopy(): SnailfishNumber {
        return this.copy(left.deepCopy(), right.deepCopy())
    }

    override fun toString(): String {
        return "[$left,$right]"
    }
}

data class RegularNumber(var value: Int) : SnailfishNumber() {
    override fun magnitude(): Long {
        return value.toLong()
    }

    override fun deepCopy(): SnailfishNumber {
        return this.copy()
    }

    override fun toString(): String {
        return value.toString()
    }
}


data class DoThingResult(val shouldNuke: Boolean)

fun explodePlease(fullS: SnailfishNumber) {
    var leftValue: RegularNumber? = null
    var valueToAdd: Int? = null
    var shouldStillExplode = true

    fun explode(s: SnailfishNumber, dep: Int): DoThingResult {
        println("Exploding $s")
        when (s) {
            is PairNumber -> {
                if (dep == 4) {
                    println("booming ${s.left} / ${s.right}")
                    //perform explosion
                    check(s.left is RegularNumber)
                    check(s.right is RegularNumber)
                    if (shouldStillExplode) {
                        leftValue?.let { it.value += (s.left as RegularNumber).value }
                        valueToAdd = (s.right as RegularNumber).value
                        println("Storing $valueToAdd to be added to next number.")
                        shouldStillExplode = false
                        return DoThingResult(shouldNuke = true)
                    } else {
                        explode(s.left, dep + 1)
                        explode(s.right, dep + 1)
                    }
                } else {
                    if (explode(s.left, dep + 1).shouldNuke) {
                        println("$s requested nuke for LEFT=${s.left}")
                        println("Pre $fullS")
                        s.left = RegularNumber(0)
                        println("Now $fullS")
                    }
                    if (explode(s.right, dep + 1).shouldNuke) {
                        println("$s requested nuke for RIGHT=${s.right}")
                        println("Pre $fullS")
                        s.right = RegularNumber(0)
                        println("Now $fullS")
                    }
                }
            }
            is RegularNumber -> {
                leftValue = s
                valueToAdd?.let {
                    println("Adding $it to $s")
                    s.value += it
                    println("now $fullS")
                    // this is always the last step. unwind the call stack
                    throw DoneException()
                }
            }
        }
        return DoThingResult(shouldNuke = false)
    }

    explode(fullS, 0)
}


fun split(s: SnailfishNumber): PairNumber? {
    when (s) {
        is PairNumber -> {
            split(s.left)?.let {
                // we split a number here!
                println("Split ${s.left} into $it")
                s.left = it
                throw DoneException()
            }
            split(s.right)?.let {
                s.right = it
                throw DoneException()
            }
        }
        is RegularNumber -> {
            if (s.value >= 10) {
                // perform split
                val left = s.value / 2
                val right = kotlin.math.ceil(s.value.toDouble() / 2.0).toInt()
                return PairNumber(RegularNumber(left), RegularNumber(right))
            }
        }
    }
    return null
}

class DoneException : Throwable()

fun reduce(theInputSFN: SnailfishNumber): SnailfishNumber {
    val curr = theInputSFN.deepCopy()
    try {
        explodePlease(curr)
    } catch (d: DoneException) {
        println("Performed explosion. Recursing.")
        println("Now $curr")
        return reduce(curr)
    }
    try {
        println("Splitting $curr")
        split(curr)
    } catch (d: DoneException) {
        println("Performed split. Recursing.")
        println("Now $curr")
        return reduce(curr)
    }
    println("Reduced without further actions.")
    return curr
}

fun main() {
    val nums = input.map {
        parseSnailNumber(StringBuilder(it))
    }
    val result = nums.reduce { acc, snailfishNumber ->
        val newNum = reduce(acc.add(snailfishNumber))
        println(newNum)
        newNum
    }
    println("RESULT$result")
    println(result.magnitude())

    val allTheMags = mutableListOf<Long>()
    for (firstNum in nums) {
        for (secondNum in nums) {
            if (firstNum == secondNum) continue
            val oneAdd = reduce(firstNum.deepCopy().add(secondNum.deepCopy()))
            allTheMags += oneAdd.magnitude()
        }
    }
    println(allTheMags.maxOf { it })
}



fun parseSnailNumber(sb: StringBuilder): SnailfishNumber {
    return when (sb.peek()) {
        '[' -> parsePair(sb)
        in '0'..'9' -> parseRegular(sb)
        else -> error("Unexpected  ${sb.peek()}")
    }
}

fun parseRegular(sb: StringBuilder): RegularNumber {
    val num = sb.takeDeleting(1).toInt()
    return RegularNumber(num)
}

fun parsePair(sb: StringBuilder): PairNumber {
    sb.deletePrefix("[")
    val first = parseSnailNumber(sb)
    sb.deletePrefix(",")
    val second = parseSnailNumber(sb)
    sb.deletePrefix("]")
    return PairNumber(first, second)
}

fun StringBuilder.peek(): Char {
    return this[0]
}

fun StringBuilder.deletePrefix(len: Int) {
    this.delete(0, len)
}

fun StringBuilder.deletePrefix(pfx: String) {
    val x = takeDeleting(pfx.length)
    check(x == pfx) { "Expected to delete '$pfx' but got '$x' instead" }
}

fun StringBuilder.takeDeleting(len: Int): String {
    val res = this.take(len)
    this.deletePrefix(len)
    return res.toString()
}