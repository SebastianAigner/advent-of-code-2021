package day25

import java.io.File

val input = File("inputs/day25.txt").readLines()
val height = input.size
val width = input[0].length

data class Vec2(val x: Int, val y: Int) {
    fun eastNeighbor(): Vec2 {
        return Vec2((x + 1) % width, y)
    }

    fun southNeighbor(): Vec2 {
        return Vec2(x, (y + 1) % height)
    }
}

enum class Facing {
    EAST,
    SOUTH
}

data class Cumber(val facing: Facing)
data class SimulationResult(val map: Map<Vec2, Cumber>, val didSimulate: Boolean)

fun simulate(map: Map<Vec2, Cumber>): SimulationResult {
    var didSimulate = false
    val east = map.filter { it.value.facing == Facing.EAST }
    // keep track of coordinates that will move this turn
    val eastMovers = mutableListOf<Vec2>()
    for (vec in east.keys) {
        if (map[vec.eastNeighbor()] == null) {
            // free space
            eastMovers += vec
            didSimulate = true
        }
    }

    val targetMap = map.toMutableMap()
    for (e in eastMovers) {
        targetMap.remove(e)
        targetMap[e.eastNeighbor()] = Cumber(facing = Facing.EAST)
    }

    val south = map.filter { it.value.facing == Facing.SOUTH }

    val southMovers = mutableListOf<Vec2>()
    for (vec in south.keys) {
        if (targetMap[vec.southNeighbor()] == null) {
            // free space
            didSimulate = true
            southMovers += vec
        }
    }

    for (s in southMovers) {
        targetMap.remove(s)
        targetMap[s.southNeighbor()] = Cumber(facing = Facing.SOUTH)
    }
    return SimulationResult(targetMap, didSimulate)
}

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val firstMap = buildMap<Vec2, Cumber> {
        for ((y, line) in input.withIndex()) {
            for ((x, char) in line.withIndex()) {
                if (char == 'v') put(Vec2(x, y), Cumber(Facing.SOUTH))
                if (char == '>') put(Vec2(x, y), Cumber(Facing.EAST))
            }
        }
    }
    firstMap.debugPrint()
    var currSim = SimulationResult(firstMap, true)
    var cnt = 0
    while (currSim.didSimulate) {
        cnt++
        currSim = simulate(currSim.map)
    }
    println(cnt)
}

fun Map<Vec2, Cumber>.debugPrint() {
    for (y in 0 until height) {
        for (x in 0 until width) {
            print(when (val f = this[Vec2(x, y)]) {
                is Cumber -> if (f.facing == Facing.SOUTH) "v" else ">"
                else -> "."
            })
        }
        println()
    }
}