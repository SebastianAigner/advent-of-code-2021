package day17

import java.io.File

val xStrYStr = File("inputs/day17.txt").readLines()[0].removePrefix("target area: ").split(", ")
val xRange = xStrYStr[0].removePrefix("x=").split("..").map { it.toInt() }.let { (a, b) -> a to b }
val yRange = xStrYStr[1].removePrefix("y=").split("..").map { it.toInt() }.let { (a, b) -> a to b }


data class Probe(var x: Int, var y: Int, var velX: Int, var velY: Int) {
    fun step() {
        x += velX
        y += velY
        if (velX > 0) velX--
        if (velX < 0) velX++
        velY--
    }

    fun hasPassed(r: Rectangle): Boolean {
        return y < r.startY
    }
}

data class Rectangle(val startX: Int, val endX: Int, val startY: Int, val endY: Int) {
    fun contains(x: Int, y: Int): Boolean {
        return x in startX..endX && y in startY..endY
    }

    fun contains(p: Probe) = contains(p.x, p.y)
}

fun main() {
    println(xRange)
    println(yRange)
    val highpoints = mutableListOf<Int>()
    for (x in 0..10000) {
        for (y in 0..10000) {
            computeHighPoint(x, y)?.let { highpoints.add(it) }
        }
    }
    println(highpoints.maxOf { it })
}

fun computeHighPoint(velX: Int, velY: Int): Int? {
    val target = Rectangle(xRange.first, xRange.second, yRange.first, yRange.second)
    val probe = Probe(0, 0, 6, 9)
    val probeSteps = mutableListOf<Probe>()
    while (!probe.hasPassed(target) && !target.contains(probe.x, probe.y)) {
        probe.step()
        probeSteps += probe.copy()
    }
    if (!target.contains(probe)) return null
    return probeSteps.maxOf { it.y }
}