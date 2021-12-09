package year2021.day09

import readInput
import kotlin.math.max

fun validAdjacentIndices(input: List<List<Int>>, y: Int, x: Int): Iterable<Pair<Int, Int>> = listOf(
    Pair(y - 1, x),
    Pair(y + 1, x),
    Pair(y, x + 1),
    Pair(y, x - 1)
).filter { (y0, x0) -> y0 >= 0 && y0 < input.size && x0 >= 0 && x0 < input[y0].size }

fun getRiskLevel(input: List<List<Int>>, y: Int, x: Int): Int = validAdjacentIndices(input, y, x)
    .map { (y0, x0) -> input[y0][x0] }
    .all { it > input[y][x] }
    .let { isLowPoint -> if (isLowPoint) input[y][x] + 1 else 0 }

fun part1(input: List<List<Int>>): Int = input
    .mapIndexed { y, row ->
        row.indices.map { x ->
            getRiskLevel(input, y, x)
        }
    }.sumOf { row -> row.sum() }

fun dfs(input: MutableList<MutableList<Int>>, y: Int, x: Int): Int = when (input[y][x]) {
    0 -> {
        // mark as visited
        input[y][x] = -1

        // check adjacent
        1 + validAdjacentIndices(input, y, x).sumOf { (y0, x0) -> dfs(input, y0, x0) }
    }
    else -> 0
}

fun part2(input: List<List<Int>>): Int {
    val heatmap = input.map { row ->
        row.map {
            max(it - 8, 0)
        }.toMutableList()
    }.toMutableList()

    val sizes = mutableListOf<Int>()
    for (i in 0 until heatmap.size) {
        for (j in 0 until heatmap[i].size) {
            if (heatmap[i][j] != 0) {
                continue
            }
            sizes.add(dfs(heatmap, i, j))
        }
    }

    sizes.sortDescending()
    return sizes.take(3).fold(1, Int::times)
}

fun main() {
    val sample = readInput(9, "sample").map { row -> row.map { it.digitToInt() } }
    val input = readInput(9, "input").map { row -> row.map { it.digitToInt() } }

    check(part1(sample) == 15)
    println(part1(input))

    check(part2(sample) == 1134)
    println(part2(input))
}
