import java.io.File

val input = File("inputs/day03.txt").readLines()
val bitIndices = input[0].indices

fun List<String>.charactersForColumn(n: Int): Map<Char, Int> = this.groupingBy { it[n] }.eachCount()
fun String.invertBinaryString() = map { if (it == '0') '1' else '0' }.joinToString("")

fun main() {
    part1()
    part2()
}

fun part1() {
    val charFrequencyByColumn = bitIndices.map { column ->
        input.charactersForColumn(column)
    }
    val combined = charFrequencyByColumn.joinToString("") {
        val (char, _) = it.maxByOrNull { it.value } ?: error("Should find max")
        char.toString()
    }
    val invertedNumber = combined.invertBinaryString()
    println(combined.toInt(2) * invertedNumber.toInt(2))
}

fun part2() {
    val oxyGenRating = input.filterColumnsByFrequency { zeroes, ones ->
        if (zeroes > ones) '0' else '1'
    }

    val co2ScrubberRating = input.filterColumnsByFrequency { zeroes, ones ->
        if (zeroes > ones) '1' else '0'
    }

    println(oxyGenRating.toInt(2) * co2ScrubberRating.toInt(2))
}

fun List<String>.filterColumnsByFrequency(predicate: (zeroes: Int, ones: Int) -> Char): String {
    var dynInput = this
    for (column in bitIndices) {
        val charFrequencyByColumn = dynInput.charactersForColumn(column)
        val zeroes = charFrequencyByColumn['0'] ?: 0
        val ones = charFrequencyByColumn['1'] ?: 0
        dynInput = dynInput.filter { it[column] == predicate(zeroes, ones) }
        if (dynInput.size == 1) break
    }
    return dynInput.joinToString("")
}