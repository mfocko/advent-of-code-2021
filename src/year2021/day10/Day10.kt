package year2021.day10

import readInput

val CLOSING: Map<Char, Char> = mapOf(
    ')' to '(', ']' to '[', '}' to '{', '>' to '<'
)
val ERROR_SCORING: Map<Char, Int> = mapOf(
    ')' to 3, ']' to 57, '}' to 1197, '>' to 25137
)
val AUTOCOMPLETE_SCORING: Map<Char, Long> = mapOf(
    '(' to 1, '[' to 2, '{' to 3, '<' to 4
)

fun check(input: String): Pair<Char?, List<Char>> {
    val stack = mutableListOf<Char>()

    for (c in input) {
        if (CLOSING.contains(c)) {
            // found a closing one
            if (stack.size < 1 || stack.last() != CLOSING[c]) {
                return Pair(c, stack)
            }
            stack.removeLast()
        } else {
            stack.add(c)
        }
    }
    return Pair(null, stack)
}

fun part1(input: List<String>): Int = input.sumOf {
    check(it).let { (c, _) ->
        ERROR_SCORING.getOrDefault(c, 0)
    }
}

fun part2(input: List<String>): Long =
    input
        .map { check(it) }
        .filter { (c, s) -> c == null && s.isNotEmpty() }
        .map { (_, stack) ->
            stack.reversed().fold(0.toLong()) { total, c ->
                5 * total + AUTOCOMPLETE_SCORING[c]!!
            }
        }.sorted().let { scores ->
            scores[scores.size / 2]
        }

fun main() {
    val sample = readInput(10, "sample")
    val input = readInput(10, "input")

    check(part1(sample) == 26397)
    println(part1(input))

    check(part2(sample) == 288957.toLong())
    println(part2(input))
}
