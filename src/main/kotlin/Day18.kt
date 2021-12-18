package day18

import java.io.File

val input = File("inputs/day18.txt").readLines()

sealed class SnailfishNumber {
    fun add(other: SnailfishNumber): PairNumber {
        return PairNumber(this, other)
    }
}

data class PairNumber(val left: SnailfishNumber, val right: SnailfishNumber) : SnailfishNumber() {
    override fun toString(): String {
        return "[$left,$right]"
    }
}

data class RegularNumber(val value: Int) : SnailfishNumber() {
    override fun toString(): String {
        return value.toString()
    }
}

fun main() {
    val nums = input.map {
        parseSnailNumber(StringBuilder(it))
    }
    println(nums.reduce { acc, snailfishNumber ->
        acc.add(snailfishNumber)
    })
}

fun reduceNumber(num: SnailfishNumber) {

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
    check(x == pfx)
}

fun StringBuilder.takeDeleting(len: Int): String {
    val res = this.take(len)
    this.deletePrefix(len)
    return res.toString()
}