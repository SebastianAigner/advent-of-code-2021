import java.io.File

val input = File("inputs/day03.txt").readLines()

fun main() {
    var outstr = ""
    for(idx in input[0].indices) {
        var zeroes = 0
        var ones = 0
        for(number in input) {
            if(number[idx] == '0') zeroes++ else ones++
        }
        outstr += if(zeroes > ones) "0" else "1"
    }
    println(outstr)
    val other = outstr.map { if(it == '0') '1' else '0' }.joinToString("")
    println(other)
    println(outstr.toInt(2) * other.toInt(2))

    part2()
}

fun part2() {
    var outstr = ""
    var dynInput = input
    for(idx in input[0].indices) {
        var zeroes = 0
        var ones = 0
        for(number in dynInput) {
            if(number[idx] == '0') zeroes++ else ones++
        }
        val popular =
            when {
                zeroes == ones -> '1'
                zeroes > ones ->'0'
                else ->'1'
            }
        dynInput = dynInput.filter { it[idx] == popular }
        println(dynInput)
        if(dynInput.size == 1) println("SDLKFJSDLKFJDLKFJD ${dynInput[0].toInt(2)}")
    }

    part22()
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