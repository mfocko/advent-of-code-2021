package day06

import readInputAsCommaSeparatedInts

fun howManyAfter(input: List<Int>, days: Int): Long =
    (1..days)
        .fold(List(9) { i -> input.count { it == i }.toLong() }) { counts, _ ->
            List(9) { i ->
                when (i) {
                    6 -> counts[0] + counts[7]
                    else -> counts[(i + 1) % 9]
                }
            }
        }
        .sum()

fun main() {
    val testInput = readInputAsCommaSeparatedInts(6, "test_input")
    val input = readInputAsCommaSeparatedInts(6, "input")

    check(howManyAfter(testInput, 80) == 5934.toLong())
    println(howManyAfter(input, 80))

    check(howManyAfter(testInput, 256) == 26984457539)
    println(howManyAfter(input, 256))
}
