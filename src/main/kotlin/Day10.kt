package day10

import java.io.File

val input = File("inputs/day10.txt").readLines()

fun main() {
    val parseResult = input.map { parseLine(it) }
    part1(parseResult)
    part2(parseResult)
}

fun part1(results: List<ParseResult>) {
    val points = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137
    )

    println(results.filterIsInstance<SyntaxError>().sumOf {
        points.getValue(it.got)
    })
}

fun part2(results: List<ParseResult>) {
    val points = mapOf(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4
    )

    val total = results.filterIsInstance<Autocomplete>().map {
        it.missingChars.fold(0L) { acc, c ->
            acc * 5 + points.getValue(c)
        }
    }.sorted()

    println(total[total.size / 2])
}

sealed class ParseResult

data class SyntaxError(val expected: Char?, val got: Char) : ParseResult()
data class Autocomplete(val missingChars: List<Char>) : ParseResult()

val mapping = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>'
)

fun parseLine(line: String): ParseResult {
    val deque = ArrayDeque<Char>()
    for (char in line) {
        val isOpening = char in mapping.keys
        if (isOpening) deque.addLast(mapping.getValue(char))
        else {
            val corresponding = deque.removeLastOrNull()
            if (corresponding != char) {
                //mismatched pair
                return SyntaxError(corresponding, char)
            }
        }
    }
    return Autocomplete(deque.reversed())
}