package year2021.day07

import readInputAsCommaSeparatedInts
import kotlin.math.absoluteValue

// for part 1, it's always included in the input set
fun part1(input: List<Int>): Int = input.toSet().minOf { target ->
    input.sumOf {
        (it - target).absoluteValue
    }
}

fun part2(input: List<Int>): Int = (input.average().toInt()).let { average ->
    (average - 1..average + 1).minOf { target ->
        input.sumOf {
            val diff = (it - target).absoluteValue
            diff * (diff + 1) / 2
        }
    }
}

fun main() {
    val testInput = readInputAsCommaSeparatedInts(7, "sample").sorted()
    val input = readInputAsCommaSeparatedInts(7, "input").sorted()

    check(part1(testInput) == 37)
    println(part1(input))

    check(part2(testInput) == 168)
    println(part2(input))
}
