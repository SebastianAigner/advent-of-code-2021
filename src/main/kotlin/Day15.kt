@file:OptIn(ExperimentalStdlibApi::class)

package day15

import java.io.File
import kotlin.math.abs

val input = File("inputs/day15.txt").readLines()

val nodes = buildList<Node> {
    for ((y, line) in input.withIndex()) {
        for ((x, char) in line.withIndex()) {
            add(Node(x, y, char.digitToInt()))
        }
    }
}

data class Node(val x: Int, val y: Int, val cost: Int)

fun main() {
    println(nodes)
    val startNode = nodes[0]
    val endNode = nodes.last()
    println(startNode)
    println(endNode)
    val dijk = dijkstra(nodes, startNode, endNode)
    println(dijk.sumOf { it.cost } - nodes[0].cost)
}

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