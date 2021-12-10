package day10

import java.io.File

val input = File("inputs/day10.txt").readLines()

fun main() {
    println(input.sumOf {
        val syntaxError = firstIllegalCharacter(it)
        if (syntaxError == null) 0 else points[syntaxError.got]!!
    })
    val incompleteLines = input.filter { firstIllegalCharacter(it) == null }
    println(incompleteLines[0])
    val total = incompleteLines.map {
        val fillerChars = completeLine(it)
        var lineScore = 0L
        for (char in fillerChars) {
            lineScore *= 5
            lineScore += part2Score[char]!!
        }
        lineScore
    }.sorted().also { println(it) }

    println(total[total.size / 2])
}

val points = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137
)

val mapping = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>'
)

// [[)
// [  (])
// [[ (]])
//

data class SytaxError(val expected: Char?, val got: Char)

fun firstIllegalCharacter(line: String): SytaxError? {
    val deque = ArrayDeque<Char>()
    for (char in line) {
        println("Looking at $char with $deque")
        val isOpeningBracket = char in mapping.keys
        if (isOpeningBracket) deque.addLast(mapping[char]!!)
        else {
            val corresponding = deque.removeLastOrNull()
            if (corresponding != char) {
                //mismatched pair
                return SytaxError(corresponding, char)
            }
        }
    }
    return null
}

val part2Score = mapOf(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4
)

fun completeLine(line: String): List<Char> {
    val deque = ArrayDeque<Char>()
    for (char in line) {
        println("Looking at $char with $deque")
        val isOpeningBracket = char in mapping.keys
        if (isOpeningBracket) {
            deque.addLast(mapping[char]!!)
        } else {
            val corresponding = deque.removeLastOrNull()
            if (corresponding != char) error("you  didn't filter right!")
        }
    }
    return deque.reversed()
}