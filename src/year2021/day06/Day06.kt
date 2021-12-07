package year2021.day06

import readInputAsCommaSeparatedInts

fun howManyAfter(input: List<Int>, days: Int): Long =
    (0 until days)
        .fold(MutableList(9) { i -> input.count { it == i }.toLong() }) { counts, day ->
            counts[(7 + day) % 9] += counts[day % 9]
            counts
        }
        .sum()

fun main() {
    val testInput = readInputAsCommaSeparatedInts(6, "sample")
    val input = readInputAsCommaSeparatedInts(6, "input")

    check(howManyAfter(testInput, 80) == 5934.toLong())
    println(howManyAfter(input, 80))

    check(howManyAfter(testInput, 256) == 26984457539)
    println(howManyAfter(input, 256))
}
