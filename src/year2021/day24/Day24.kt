package year2021.day24

import readInput

data class Parameter(val remainder: Long, val divisor: Long, val addition: Long)

fun solver(range: LongProgression, parameters: List<Parameter>): Long {
    val cache: MutableMap<Triple<Int, Long, Long>, Long?> = mutableMapOf()

    fun solveAlternative(i: Int, w: Long, z: Long, overall: Long = 0): Long? {
        val cacheKey = Triple(i, w, z)
        if (cacheKey in cache) {
            return cache[cacheKey];
        }

        val (remainder, divisor, addition) = parameters[i]
        val x = if (((z % 26) + remainder) != w) 1 else 0

        val newZ = z / divisor * (25 * x + 1) + (w + addition) * x
        val result = when (i) {
            13 -> if (newZ == 0L) overall else null
            else -> range.firstNotNullOfOrNull {
                solveAlternative(i + 1, it, newZ, overall * 10 + it)
            }
        }

        cache[cacheKey] = result
        return result
    }

    return range.firstNotNullOf { solveAlternative(0, it, 0, it) }
}

fun part1(parameters: List<Parameter>): Long = solver(9L downTo 1L, parameters)
fun part2(parameters: List<Parameter>): Long = solver(1L..9L, parameters)

fun List<String>.toParameters(): List<Parameter> = this.chunked(18).map {
    val (rem, div, add) = listOf(it[5], it[4], it[15]).map { it.split(" ")[2].toLong() }
    Parameter(rem, div, add)
}

fun main() {
    val parameters = readInput(24, "input").toParameters()
    println(part1(parameters))
    println(part2(parameters))
}
