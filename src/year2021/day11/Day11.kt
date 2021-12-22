package year2021.day11

import product
import readInput

fun validAdjacentIndices(input: List<List<Int>>, y0: Int, x0: Int): Sequence<Pair<Int, Int>> =
    product(y0 - 1..y0 + 1, x0 - 1..x0 + 1)
        .filter { (y, x) ->
            (y != y0 || x != x0) && (y >= 0 && y < input.size) && (x >= 0 && x < input[y].size)
        }

fun allIndices(octopuses: List<List<Int>>): Sequence<Pair<Int, Int>> =
    product(octopuses.indices, octopuses.first().indices)

fun getFlashes(octopuses: List<List<Int>>): Sequence<Pair<Int, Int>> =
    allIndices(octopuses).filter { (y, x) -> octopuses[y][x] > 9 }

fun step(octopuses: List<MutableList<Int>>): Int {
    // First, the energy level of each octopus increases by 1.
    for ((y, x) in allIndices(octopuses)) {
        octopuses[y][x]++
    }

    // Then, any octopus with an energy level greater than 9 flashes.
    val queue = ArrayDeque(getFlashes(octopuses).toList())
    while (queue.isNotEmpty()) {
        val (y, x) = queue.removeFirst()

        for ((y_1, x_1) in validAdjacentIndices(octopuses, y, x)) {
            octopuses[y_1][x_1]++

            // == 10 means we made it flash
            if (octopuses[y_1][x_1] == 10) {
                queue.addLast(Pair(y_1, x_1))
            }
        }
    }

    // Count the flashing octopuses.
    val flashes = octopuses.sumOf { row -> row.count { it > 9 } }

    // Reset energy level.
    for ((y, x) in allIndices(octopuses)) {
        if (octopuses[y][x] > 9) {
            octopuses[y][x] = 0
        }
    }

    return flashes
}

fun part1(input: List<List<Int>>): Int {
    val octopuses = input.map { row -> row.toMutableList() }
    return (1..100).sumOf { step(octopuses) }
}

fun part2(input: List<List<Int>>): Int {
    val octopuses = input.map { row -> row.toMutableList() }
    val count = octopuses.size * octopuses[0].size
    return (1..Int.MAX_VALUE).first { step(octopuses) == count }
}

fun main() {
    val sample = readInput(11, "sample").map { row -> row.map { it.digitToInt() } }
    val input = readInput(11, "input").map { row -> row.map { it.digitToInt() } }

    check(part1(sample) == 1656)
    println(part1(input))

    check(part2(sample) == 195)
    println(part2(input))
}
