@file:OptIn(ExperimentalStdlibApi::class)

package day15

import java.io.File
import kotlin.math.abs

val input = File("inputs/day15.txt").readLines()
val width = input[0].length
val height = input.size

fun getValFor(x: Int, y: Int): Int {
    val rX = x % width
    val rY = y % height
    val charAt = input[rY][rX].digitToInt()
    val xBlock = (x / width)
    val yBlock = (y / height)
    if (xBlock == 0 && yBlock == 0) return charAt
    val prevVal = when {
        xBlock > 0 -> getValFor(x - width, y)
        yBlock > 0 -> getValFor(x, y - height)
        else -> error("broken")
    }
    val newVal = prevVal + 1
    val realNewVal = if (newVal == 10) 1 else newVal
    return realNewVal
}

fun getNodeFor(x: Int, y: Int): Node {
    return Node(x, y, getValFor(x, y))
}

val nodes = buildList<Node> {
    repeat(5 * height) { y ->
        repeat(5 * width) { x ->
            add(getNodeFor(x, y))
        }
    }
}

data class Node(val x: Int, val y: Int, val cost: Int)

fun main() {
    val startNode = nodes[0]
    val endNode = nodes.last()
    println(startNode)
    println(endNode)
    val dijk = dijkstra(nodes, startNode, endNode)
    println(dijk.sumOf { it.cost } - nodes[0].cost)
}

// This impl is slow :) It might be worth refactoring to a solution like the one from Roman: https://github.com/elizarov/AdventOfCode2021/blob/main/src/Day15_2_fast.kt
// But it _did_ get the right solution in the end.

fun dijkstra(graph: List<Node>, source: Node, target: Node): ArrayDeque<Node> {
    val q = mutableSetOf<Node>()
    val dist = mutableMapOf<Node, Int?>()
    val prev = mutableMapOf<Node, Node?>()
    for (vertex in graph) {
        dist[vertex] = Int.MAX_VALUE
        prev[vertex] = null
        q.add(vertex)
    }
    dist[source] = 0
    while (q.isNotEmpty()) {
        val u = q.minByOrNull { dist[it]!! }!!
        q.remove(u)
        if (u == target) break
        for (v in q.filter { it.isNeighborOf(u) }) {
            val alt = dist[u]!! + length(u, v)
            if (alt < dist[v]!!) {
                dist[v] = alt
                prev[v] = u
            }
        }
    }

    // all found.

    val s = ArrayDeque<Node>()
    var u: Node? = target
    if (prev[u] != null || u == source) {
        while (u != null) {
            s.addFirst(u)
            u = prev[u]
        }
    }

    return s
}

fun Node.isNeighborOf(u: Node): Boolean {
    val xDist = abs(this.x - u.x)
    val yDist = abs(this.y - u.y)
    return xDist + yDist == 1
}

fun length(u: Node, v: Node): Int {
    // the cost is saved in v
    return v.cost
}