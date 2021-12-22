package day22

import java.io.File
import kotlin.math.max
import kotlin.math.min

val input = File("inputs/day22.txt").readLines().map {
    val (inst, rest) = it.split(" ")
    val (x, y, z) = rest.split(",")
    val (xMin, xMax) = x.removePrefix("x=").split("..").map { it.toInt() }
    val (yMin, yMax) = y.removePrefix("y=").split("..").map { it.toInt() }
    val (zMin, zMax) = z.removePrefix("z=").split("..").map { it.toInt() }
    CuboidRule(
        if (inst == "on") Instruction.ON else Instruction.OFF,
        xMin..xMax,
        yMin..yMax,
        zMin..zMax
    )
}

data class Vec3(val x: Int, val y: Int, val z: Int)

enum class Instruction {
    ON,
    OFF
}

data class CuboidRule(val instruction: Instruction, val xRange: IntRange, val yRange: IntRange, val zRange: IntRange)

fun main() {
    println(input)
    val c = Cubismo()
    for (rule in input) {
        c.setState(rule.xRange, rule.yRange, rule.zRange, rule.instruction)
        println(rule)
        println("sum " + c.cuboids.sumOf {
            if (it.instruction == Instruction.OFF) 0L else
                it.cuboid.volume()
        })
    }


}

data class ReactorState(val cuboid: Cuboid, val instruction: Instruction) {
    override fun toString(): String {
        return "($cuboid=$instruction)"
    }
}

data class Cuboid(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    override fun toString(): String {
        return "[x=$xRange,y=$yRange,z=$zRange]"
    }

    fun volume(): Long {
        // todo:
        // handle negative values
        try {
            check(xRange.last >= xRange.first)
            check(yRange.last >= yRange.first)
            check(zRange.last >= zRange.first)
        } catch (x: IllegalStateException) {
            println("BANG: $xRange $yRange $zRange")
            return 0
        }

        return (xRange.last.toLong() - xRange.first.toLong() + 1) *
                (yRange.last.toLong() - yRange.first.toLong() + 1) *
                (zRange.last.toLong() - zRange.first.toLong() + 1)
    }

    fun overlaps(other: Cuboid): Boolean {
        return xRange.overlaps(other.xRange) || yRange.overlaps(other.yRange) || zRange.overlaps(other.zRange)
    }

//    fun isSubcuboidOf(other: Cuboid): Boolean {
//        return xRange.first >= other.xRange.first && xRange.last <= other.xRange.last
//                && yRange.first >= other.yRange.first && yRange.last <= other.yRange.last
//                && zRange.first >= other.zRange.first && zRange.last <= other.zRange.last
//    }
}

class Cubismo() {
    // Cubismo stores a list of cuboids
    var cuboids = mutableSetOf<ReactorState>()
    fun setState(xRange: IntRange, yRange: IntRange, zRange: IntRange, instruction: Instruction) {
        // see if we have any overlap
        val innerCuboid = Cuboid(xRange, yRange, zRange)
        val overlaps = cuboids.filter { it.cuboid.overlaps(innerCuboid) }
        if (overlaps.isEmpty()) {
            // there's no overlap, separate cuboid
            cuboids += ReactorState(innerCuboid, instruction)
            return
        }
        // we need to do overlap resolution
        for (outerCuboid in overlaps) {
            println("$innerCuboid $instruction overlaps with $outerCuboid")
            val unchangedNewCuboids = dissectOuterCuboid(outerCuboid.cuboid, innerCuboid)
            cuboids -= outerCuboid

            val newboids = unchangedNewCuboids.map { ReactorState(it, outerCuboid.instruction) }
                .filter { it.cuboid.volume() > 0 } + ReactorState(innerCuboid, instruction)
            println("$outerCuboid+$innerCuboid turned into $newboids")
            cuboids += newboids
        }
    }

    fun dissectOuterCuboid(outerCuboid: Cuboid, innerCuboid: Cuboid): List<Cuboid> {
        // it's probably enough to create 6 other pillars:
        // two "sandwiches slices" on each side of the cube
        // four cuboids surrounding the "meat" of the sandwich (the new cuboid)
        // the "left" sandwich slice:
        // extends fully in y and z
        // extends from outerCuboid.xStart to innerCuboid.xStart (non-incl)
        val leftSandwich =
            Cuboid(
                outerCuboid.xRange.first until innerCuboid.xRange.first,
                outerCuboid.yRange,
                outerCuboid.zRange
            )

        val rightSandwich =
            Cuboid(
                innerCuboid.xRange.last + 1..outerCuboid.xRange.last,
                outerCuboid.yRange,
                outerCuboid.zRange
            )

        val topPickle =
            Cuboid(
                innerCuboid.xRange,
                outerCuboid.yRange,
                innerCuboid.zRange.last + 1..outerCuboid.zRange.last
            )

        val bottomPickle =
            Cuboid(
                innerCuboid.xRange,
                outerCuboid.yRange,
                outerCuboid.zRange.first until innerCuboid.zRange.first
            )

        val frontFry =
            Cuboid(
                innerCuboid.xRange,
                outerCuboid.yRange.first until innerCuboid.yRange.first,
                innerCuboid.zRange
            )
        val backFry =
            Cuboid(
                innerCuboid.xRange,
                innerCuboid.yRange.last + 1..outerCuboid.yRange.last,
                innerCuboid.zRange
            )

        val dissect = listOf(leftSandwich, rightSandwich, topPickle, bottomPickle, frontFry, backFry)
        println("Dissected to $dissect")
        return dissect
    }
}

fun part1() {
    val map = mutableMapOf<Vec3, Instruction>()
    for (rule in input) {
        for (x in rule.xRange.clamp(-50, 50)) {
            for (y in rule.yRange.clamp(-50, 50)) {
                for (z in rule.zRange.clamp(-50, 50)) {
                    map[Vec3(x, y, z)] = rule.instruction
                }
            }
        }
    }
    println(map.values.groupingBy { it }.eachCount())
}

fun IntRange.clamp(min: Int, max: Int): IntRange {
    return max(min, this.first)..min(max, this.last)
}

fun IntRange.overlaps(other: IntRange): Boolean {
    return this.first <= other.last && this.last >= other.first
}