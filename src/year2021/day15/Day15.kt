package year2021.day15

import readInput
import java.util.*

fun validAdjacentIndices(input: List<List<Int>>, y0: Int, x0: Int): Iterable<Pair<Int, Int>> =
    (y0 - 1..y0 + 1)
        .flatMap { y -> (x0 - 1..x0 + 1).map { x -> Pair(y, x) } }
        .filter { (y, x) ->
            (y != y0 || x != x0) && (y == y0 || x == x0) && (y >= 0 && y < input.size) && (x >= 0 && x < input[y].size)
        }

fun validAdjacentIndicesExtended(input: List<List<Int>>, y0: Int, x0: Int): Iterable<Pair<Int, Int>> =
    (y0 - 1..y0 + 1)
        .flatMap { y -> (x0 - 1..x0 + 1).map { x -> Pair(y, x) } }
        .filter { (y, x) ->
            (y != y0 || x != x0) && (y == y0 || x == x0) && (y >= 0 && y < 5 * input.size) && (x >= 0 && x < 5 * input[0].size)
        }

data class Coordinate(val y: Int, val x: Int)

fun part1(input: List<List<Int>>): Int {
    val final = Coordinate(input.size - 1, input.last().size - 1)

    val queue = PriorityQueue<Pair<Int, Coordinate>>() { a, b ->
        a.first - b.first
    }
    val distances = mutableMapOf<Coordinate, Int>()
    val processed = mutableSetOf<Coordinate>()

    queue.add(Pair(0, Coordinate(0, 0)))
    distances[Coordinate(0, 0)] = 0
    for (y in input.indices) {
        for (x in input[y].indices) {
            queue.add(Pair(Int.MAX_VALUE, Coordinate(y, x)))
        }
    }

    while (queue.isNotEmpty()) {
        val (distance, coordinate) = queue.remove()!!
        if (processed.contains(coordinate)) {
            continue
        }
        processed.add(coordinate)

        if (coordinate == final) {
            return distance
        }

        for ((y, x) in validAdjacentIndices(input, coordinate.y, coordinate.x)) {
            val neighbour = Coordinate(y, x)
            val alternative = distance + input[y][x]

            if (alternative < (distances[neighbour] ?: Int.MAX_VALUE)) {
                distances[neighbour] = alternative
                queue.add(Pair(alternative, neighbour))
            }
        }
    }
    return Int.MAX_VALUE
}

fun getDistance(input: List<List<Int>>, y: Int, x: Int): Int {
    val riskLevel = input[y % input.size][x % input[0].size]
    val added = y / input.size + x / input[0].size

    if (riskLevel + added > 9) {
        return riskLevel + added - 9
    }

    return riskLevel + added
}

fun part2(input: List<List<Int>>): Int {
    val final = Coordinate(5 * input.size - 1, 5 * input.last().size - 1)

    val queue = PriorityQueue<Pair<Int, Coordinate>>() { a, b ->
        a.first - b.first
    }
    val distances = mutableMapOf<Coordinate, Int>()
    val processed = mutableSetOf<Coordinate>()

    queue.add(Pair(0, Coordinate(0, 0)))
    distances[Coordinate(0, 0)] = 0
    for (y in input.indices) {
        for (x in input[y].indices) {
            for (i in 0 until 5) {
                queue.add(Pair(Int.MAX_VALUE, Coordinate(i * y, i * x)))
            }
        }
    }

    while (queue.isNotEmpty()) {
        val (distance, coordinate) = queue.remove()!!
        if (processed.contains(coordinate)) {
            continue
        }
        processed.add(coordinate)

        if (coordinate == final) {
            return distance
        }

        for ((y, x) in validAdjacentIndicesExtended(input, coordinate.y, coordinate.x)) {
            val neighbour = Coordinate(y, x)
            val alternative = distance + getDistance(input, y, x)

            if (alternative < (distances[neighbour] ?: Int.MAX_VALUE)) {
                distances[neighbour] = alternative
                queue.add(Pair(alternative, neighbour))
            }
        }
    }
    return Int.MAX_VALUE
}

fun main() {
    val sample = readInput(15, "sample").map { row -> row.map { it.digitToInt() } }
    val input = readInput(15, "input").map { row -> row.map { it.digitToInt() } }

    check(part1(sample) == 40)
    println(part1(input))

    check(part2(sample) == 315)
    println(part2(input))
}
