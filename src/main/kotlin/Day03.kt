import java.io.File

val input = File("inputs/day03.txt").readLines()

fun List<String>.charactersForColumn(n: Int): Map<Char, Int> {
    val frequency = mutableMapOf<Char, Int>()
    for(item in this) {
        val digit = item[n]
        frequency[digit] = (frequency[digit] ?: 0) + 1
    }
    return frequency
}

fun main() {
    part1()
    part2()
}

fun part1() {
    val charFrequencyByColumn = input[0].indices.map { column ->
        input.charactersForColumn(column)
    }
    val combined = charFrequencyByColumn.joinToString("") {
        val (char, _) = it.maxByOrNull { it.value } ?: error("Should find max")
        char.toString()
    }
    val invertedNumber = combined.map { if(it == '0') '1' else '0' }.joinToString("")
    println(combined.toInt(2) * invertedNumber.toInt(2))
}

fun part2() {
    val oxyGenRating = input.foo { list, column, zeroes, ones ->
        val popular =
            when {
                zeroes > ones ->'0'
                zeroes == ones -> '1'
                else ->'1'
            }
        list.filter { it[column] == popular }
    }

    val co2ScrubberRating = input.foo { list, column, zeroes, ones ->
        val popular =
            when {
                zeroes > ones ->'1'
                zeroes == ones -> '0'
                else ->'0'
            }
        list.filter { it[column] == popular }
    }

    println(oxyGenRating.toInt(2) * co2ScrubberRating.toInt(2))
}

fun List<String>.foo(filter: (list: List<String>, column: Int, zeroes: Int, ones: Int) -> List<String>): String {
    var dynInput = this
    for(column in this[0].indices) {
        val charFrequencyByColumn = dynInput.charactersForColumn(column)
        val zeroes = charFrequencyByColumn['0'] ?: 0
        val ones = charFrequencyByColumn['1'] ?: 0
        dynInput = filter(dynInput, column, zeroes, ones)
        if(dynInput.size == 1) break
    }
    return dynInput.joinToString("")
}