import java.io.File

val input = File("inputs/day03.txt").readLines()
val bitIndices = input[0].indices

fun List<String>.charactersForColumn(n: Int): Map<Char, Int> = this.groupingBy { it[n] }.eachCount()
fun String.invertBinaryString() = this.map { if (it == '0') '1' else '0' }.joinToString("")

fun main() {
    part1()
    part2()
}

fun part1() {
    val charFrequencyByColumn = bitIndices.map { column ->
        input.charactersForColumn(column)
    }
    val gammaRate = charFrequencyByColumn.joinToString("") { frequencies ->
        val mostFrequentChar = frequencies
            .maxByOrNull { it.value }
            ?.key
            ?: error("Should find maximum in $frequencies!")
        mostFrequentChar.toString()
    }
    val epsilonRate = gammaRate.invertBinaryString()
    println(gammaRate.toInt(2) * epsilonRate.toInt(2))
}

fun part2() {
    val oxyGenRating = input.filterColumnsForCharacter { zeroes, ones ->
        if (zeroes > ones) '0' else '1'
    }

    val co2ScrubberRating = input.filterColumnsForCharacter { zeroes, ones ->
        if (zeroes > ones) '1' else '0'
    }

    println(oxyGenRating.toInt(2) * co2ScrubberRating.toInt(2))
}

fun List<String>.filterColumnsForCharacter(desiredCharacterByFrequency: (zeroes: Int, ones: Int) -> Char): String {
    var candidateList = this
    for (column in bitIndices) {
        val charFrequencyByColumn = candidateList.charactersForColumn(column)
        val zeroes = charFrequencyByColumn['0'] ?: 0
        val ones = charFrequencyByColumn['1'] ?: 0
        candidateList = candidateList.filter { it[column] == desiredCharacterByFrequency(zeroes, ones) }
        if (candidateList.size == 1) break
    }
    return candidateList.single()
}