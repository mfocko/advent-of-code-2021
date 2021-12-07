package year2021.day01

import readInputAsInts

fun main() {
    fun part1(input: List<Int>): Int = input.windowed(2).count { it[0] < it[1] }
    fun part2(input: List<Int>): Int = input.windowed(4).count { it[0] < it[3] }

    val testInput = readInputAsInts(1, "sample")
    val input = readInputAsInts(1, "input")

    check(part1(testInput) == 7)
    println(part1(input))

    check(part2(testInput) == 5)
    println(part2(input))
}
