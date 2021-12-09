package year2021.day09

import readInput
import kotlin.math.max

fun getRiskLevel(input: List<List<Int>>, y: Int, x: Int): Int = listOf(
    Pair(y - 1, x),
    Pair(y + 1, x),
    Pair(y, x + 1),
    Pair(y, x - 1)
).filter { (y0, x0) -> y0 >= 0 && y0 < input.size && x0 >= 0 && x0 < input[y0].size }
    .map { (y0, x0) -> input[y0][x0] }
    .all { it > input[y][x] }.let { isLowPoint -> if (isLowPoint) input[y][x] + 1 else 0 }

fun part1(input: List<List<Int>>): Int = input
    .mapIndexed { y, row ->
        row.mapIndexed { x, col ->
            getRiskLevel(input, y, x)
        }
    }.sumOf { row -> row.sum() }

fun dfs(input: MutableList<MutableList<Int>>, y: Int, x: Int): Int {
    if (input[y][x] != 0) {
        return 0
    }

    input[y][x] = -1

    var total = 1
    listOf(
        Pair(y - 1, x),
        Pair(y + 1, x),
        Pair(y, x + 1),
        Pair(y, x - 1)
    ).filter { (y0, x0) -> y0 >= 0 && y0 < input.size && x0 >= 0 && x0 < input[y0].size }
        .forEach { (y0, x0) -> total += dfs(input, y0, x0) }

    return total;
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
    println(sizes)

    return sizes.take(3).fold(1) { product, n -> n * product }
}

fun main() {
    val sample = readInput(9, "sample").map { row -> row.map { it.digitToInt() } }
    val input = readInput(9, "input").map { row -> row.map { it.digitToInt() } }

    check(part1(sample) == 15)
    println(part1(input))

    check(part2(sample) == 1134)
    println(part2(input))
}
