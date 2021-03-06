package year2021.day12

import readGraph

fun findAllPaths(
    graph: Map<String, Set<String>>,
    path: List<String>,
    visited: Set<String>,
    twiceVisited: Boolean
): Int {
    if (path.last() == "end") {
        return 1
    }

    val neighbours = graph
        .getOrDefault(path.last(), emptySet())
        .filter { it == it.uppercase() || (!twiceVisited && it != "start") || !visited.contains(it) }
        .map { it to ((it == it.lowercase() && visited.contains(it)) || twiceVisited) }

    return neighbours.map { (neighbour, newTwiceVisited) ->
        findAllPaths(graph, path + neighbour, visited + neighbour, newTwiceVisited)
    }.fold(0, Int::plus)
}

fun part1(input: Map<String, Set<String>>): Int =
    findAllPaths(input, listOf("start"), setOf("start"), true)

fun part2(input: Map<String, Set<String>>): Int =
    findAllPaths(input, listOf("start"), setOf("start"), false)

fun main() {
    val sample = readGraph(12, "sample")
    val slightlyLargerSample = readGraph(12, "slightly_larger_sample")
    val evenLargerExample = readGraph(12, "even_larger_sample")
    val input = readGraph(12, "input")

    check(part1(sample) == 10)
    check(part1(slightlyLargerSample) == 19)
    check(part1(evenLargerExample) == 226)
    println(part1(input))

    check(part2(sample) == 36)
    check(part2(slightlyLargerSample) == 103)
    check(part2(evenLargerExample) == 3509)
    println(part2(input))
}
