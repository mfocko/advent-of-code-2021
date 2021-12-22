package year2021.day08

import product
import readInput

class Patterns(private val patterns: MutableList<String>) {
    private val segments: List<Set<Char>> = listOf(
        "abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg"
    ).map { it.toSet() }
    private val mapping: Map<Int, List<Int>> = mapOf(
        2 to listOf(1), 3 to listOf(7), 4 to listOf(4), 7 to listOf(8), 6 to listOf(0, 6, 9), 5 to listOf(2, 3, 5)
    )

    private val usedDigits: MutableList<Boolean> = (0..9).map { false }.toMutableList()
    private var decoded: MutableMap<Int, Set<Char>> = mutableMapOf()

    constructor(patternsAsString: String) : this(patternsAsString.split(" ").toMutableList())

    private fun preprocess() {
        val toRemove = mutableSetOf<String>()

        patterns.forEach { pattern ->
            val options = mapping[pattern.length] ?: listOf()
            if (options.size == 1) {
                decoded[options.first()] = pattern.toSet()
                usedDigits[options.first()] = true
                toRemove.add(pattern)
            }
        }

        toRemove.forEach { patterns.remove(it) }
    }

    private fun check(): Boolean =
        product(segments.indices, segments.indices)
            .all { (i, j) ->
                segments[i].intersect(segments[j]).size == (decoded[i] ?: emptySet()).intersect(
                    (decoded[j] ?: emptySet()).toSet()
                ).size
            }

    private fun resolve(i: Int): Boolean {
        if (i == patterns.size) {
            return check()
        }

        for (possibleDigit in mapping[patterns[i].length]!!) {
            if (usedDigits[possibleDigit]) {
                continue
            }

            decoded[possibleDigit] = patterns[i].toSet()
            usedDigits[possibleDigit] = true

            if (resolve(i + 1)) {
                return true
            }
            usedDigits[possibleDigit] = false
        }

        return false
    }

    fun decode(allDigits: Boolean = false): MutableMap<Int, Set<Char>> {
        preprocess()
        if (allDigits) {
            resolve(0)
        }
        return decoded
    }
}

fun part1(input: List<String>): Int = input.sumOf { entry ->
    val (patterns, output) = entry.split(" | ")
    val decodedPatterns = Patterns(patterns).decode()

    output
        .split(" ")
        .count { decodedPatterns.values.contains(it.toSet()) }
}

fun part2(input: List<String>): Int = input.sumOf { entry ->
    val (patterns, output) = entry.split(" | ")
    val decodedPatterns = Patterns(patterns)
        .decode(true)
        .map { (key, value) -> Pair(value, key) }
        .toMap()

    output
        .split(" ")
        .map { it.toSet() }
        .fold(0) { n, digit -> 10 * n + decodedPatterns[digit]!! }
        .toLong()
}.toInt()

fun main() {
    val sample = readInput(8, "sample")
    val input = readInput(8, "input")

    check(part1(sample) == 26)
    println(part1(input))

    check(part2(listOf("acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf")) == 5353)
    check(part2(sample) == 61229)
    println(part2(input))
}
