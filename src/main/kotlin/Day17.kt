@file:OptIn(ExperimentalStdlibApi::class)

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

data class SimResult(val velX: Int, val velY: Int, val highpoint: Int)

fun main() {
    val xes = computeXPower()
    val yes = computeYPower()
    println(xes)
    println(yes)
    val combis = buildList {
        for (x in xes) {
            for (y in yes) {
                if (computeHighPoint(x, y) != null) {
                    add(x to y)
                }
            }
        }
    }
    println(combis.size)

    val results = mutableListOf<SimResult>()
    for (x in xes) {
        for (y in yes) {
            val hp = computeHighPoint(x, y)
            if (hp != null) {
                results += SimResult(x, y, hp)
            }
        }
    }
    println(results.maxByOrNull { it.highpoint })
}

fun computeYPower(): Set<Int> {
    val (lowerBound, upperBound) = yRange
    val yPowers = mutableSetOf<Int>()
    power@ for (startVel in -10000..10000) {
        var y = 0
        var yVel = startVel
        do {
            y += yVel
            yVel--
            if (y in lowerBound..upperBound) {
                // target hit
                yPowers += startVel
                continue@power
            }
        } while (y >= lowerBound)
    }

    return yPowers
}


fun computeXPower(): Set<Int> {
    val (lowerBound, upperBound) = xRange
    val xPowers = mutableSetOf<Int>()
    power@ for (startVel in 0..upperBound) {
        var x = 0
        var xVel = startVel
        do {
            x += xVel
            if (xVel > 0) xVel--
            if (xVel < 0) xVel++
            if (x in lowerBound..upperBound) {
                // target hit
                xPowers += startVel
                continue@power
            }
        } while (x <= upperBound && xVel != 0)
    }

    return xPowers
}

fun computeHighPoint(velX: Int, velY: Int): Int? {
    val target = Rectangle(xRange.first, xRange.second, yRange.first, yRange.second)
    val probe = Probe(0, 0, velX, velY)
    val probeSteps = mutableListOf<Probe>()
    while (!probe.hasPassed(target) && !target.contains(probe.x, probe.y)) {
        probe.step()
        probeSteps += probe.copy()
    }
    if (!target.contains(probe)) return null
    return probeSteps.maxOf { it.y }
}