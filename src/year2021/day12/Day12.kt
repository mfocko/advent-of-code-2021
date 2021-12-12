package year2021.day12

import readGraph

fun findAllPaths(graph: Map<String, Set<String>>, path: List<String>, visited: Set<String>): Set<String> {
    if (path.last() == "end") {
        return setOf(path.joinToString("-"))
    }

    val neighbours = graph
        .getOrDefault(path.last(), emptySet())
        .filter { it == it.uppercase() || !visited.contains(it) }

    return neighbours.map { neighbour ->
        findAllPaths(graph, path + neighbour, visited + neighbour)
    }.fold(emptySet()) { acc, it -> acc + it }
}

fun part1(input: Map<String, Set<String>>): Int =
    findAllPaths(input, listOf("start"), setOf("start")).size

fun findAllPaths(
    graph: Map<String, Set<String>>,
    path: List<String>,
    visited: Set<String>,
    twiceVisited: Boolean
): Set<String> {
    if (path.last() == "end") {
        return setOf(path.joinToString("-"))
    }

    val neighbours = graph
        .getOrDefault(path.last(), emptySet())
        .filter { it == it.uppercase() || (!twiceVisited && it != "start") || !visited.contains(it) }

    return neighbours.map { neighbour ->
        if (neighbour == neighbour.lowercase() && visited.contains(neighbour))
            findAllPaths(graph, path + neighbour, visited, true)
        else
            findAllPaths(graph, path + neighbour, visited + neighbour, twiceVisited)
    }.fold(emptySet()) { acc, it -> acc + it }
}

fun part2(input: Map<String, Set<String>>): Int = findAllPaths(input, listOf("start"), setOf("start"), false).size

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
