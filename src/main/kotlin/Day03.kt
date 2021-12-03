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
    val charFrequencyByColumn = input[0].indices.map { column ->
        input.charactersForColumn(column)
    }
    val combined = charFrequencyByColumn.joinToString("") {
        val (char, _) = it.maxByOrNull { it.value } ?: error("Should find max")
        char.toString()
    }
    val invertedNumber = combined.map { if(it == '0') '1' else '0' }.joinToString("")
    println(combined.toInt(2) * invertedNumber.toInt(2))
    part2()
}

fun part2() {
    var dynInput = input
    for(column in input[0].indices) {
        val charFrequencyByColumn = dynInput.charactersForColumn(column)
        val zeroes = charFrequencyByColumn['0'] ?: 0
        val ones = charFrequencyByColumn['1'] ?: 0
        val popular =
            when {
                zeroes > ones ->'0'
                zeroes == ones -> '1'
                else ->'1'
            }
        dynInput = dynInput.filter { it[column] == popular }
        if(dynInput.size == 1) break
    }

    val oxyGenRating = dynInput.single()


    dynInput = input
    for(column in input[0].indices) {
        val charFrequencyByColumn = dynInput.charactersForColumn(column)
        val zeroes = charFrequencyByColumn['0'] ?: 0
        val ones = charFrequencyByColumn['1'] ?: 0
        val popular =
            when {
                zeroes > ones ->'1'
                zeroes == ones -> '0'
                else ->'0'
            }
        dynInput = dynInput.filter { it[column] == popular }
        if(dynInput.size == 1) break
    }

    val co2ScrubberRating = dynInput.single()
    println(oxyGenRating.toInt(2) * co2ScrubberRating.toInt(2))
}

fun part22() {
    var outstr = ""
    var dynInput = input
    for(idx in input[0].indices) {
        var zeroes = 0
        var ones = 0
        for(number in dynInput) {
            if(number[idx] == '0') zeroes++ else ones++
        }
        val unpopular =
            when {
                zeroes == ones -> '0'
                zeroes > ones ->'1'
                else ->'0'
            }
        dynInput = dynInput.filter { it[idx] == unpopular }
        println(dynInput)
        if(dynInput.size == 1) println("SDLKFJSDLKFJDLKFJD ${dynInput[0].toInt(2)}")
    }
}