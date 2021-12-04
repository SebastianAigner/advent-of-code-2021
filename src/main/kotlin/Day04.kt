package day04

import java.io.File

val input = File("inputs/day04.txt").readLines()
val draws = input[0].split(",").map { it.toInt() }
val boards = input
    .drop(1)
    .chunked(6)
    .map { singleBoardLines ->
        singleBoardLines.filter { singleBoardLine ->
            singleBoardLine.isNotBlank()
        }
    }

val cleanedInput = boards.map { board ->
    board.map { line ->
        line.split(" ", "  ")
            .filter { it.isNotBlank() } // Leading whitespace
            .map { it.toInt() }
    }
}

fun main() {
    var bingoBoards = cleanedInput.map { BingoBoard.fromCollection(it) }

    // Prints all winners in order of appearance.
    // Part 1 is the first winner.
    // Part 2 is the last winner.
    for (currentDraw in draws) {
        for (board in bingoBoards) {
            board.mark(currentDraw)
            if (board.checkBoard()) {
                val sumOfUnmarkedFields = board.unmarked().sum()
                println(sumOfUnmarkedFields * currentDraw)
                bingoBoards = bingoBoards.filter { it != board } // remove winners
            }
        }
    }
}

data class BingoField(val value: Int, val marked: Boolean = false)

data class BingoBoard(val fields: List<MutableList<BingoField>>) {

    private val widthIndices = fields[0].indices
    private val heightIndices = fields.indices

    companion object {
        fun fromCollection(coll: List<List<Int>>): BingoBoard {
            return BingoBoard(coll.map { row -> row.map { field -> BingoField(field) }.toMutableList() })
        }
    }

    fun checkBoard() = checkRow() || checkColumn()
    private fun checkRow() = fields.any { row -> row.all { it.marked } }

    private fun checkColumn(): Boolean {
        for (column in widthIndices) {
            var columnOk = true
            for (row in heightIndices) {
                if (!fields[row][column].marked) {
                    columnOk = false
                    continue
                }
            }
            if (columnOk) return true
        }
        return false
    }

    fun mark(num: Int) {
        for (row in this.fields) {
            row.replaceAll {
                if (it.value == num) it.copy(marked = true) else it
            }
        }
    }

    fun unmarked() = fields.flatten().filter { !it.marked }.map { it.value }
}