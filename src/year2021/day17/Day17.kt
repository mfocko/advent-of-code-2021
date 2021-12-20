package year2021.day17

import readInput
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.max
import kotlin.math.sign

data class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector): Vector = Vector(x + other.x, y + other.y)

    fun updateVelocity(): Vector = Vector(x - x.sign, y - 1)
}

data class Area(val min: Vector, val max: Vector) {
    fun has(z: Vector): Boolean = (z.x in min.x..max.x) && (z.y in min.y..max.y)

    private fun speeds(getter: (Vector) -> Int): IntRange =
        max(getter(min).absoluteValue, getter(max).absoluteValue).let { absMax ->
            (min(absMax * getter(min).sign, 0)..absMax)
        }

    val xSpeeds: IntRange
        get() = speeds { it.x }

    val ySpeeds: IntRange
        get() = speeds { it.y }

    val possibleVectors: Iterable<Vector>
        get() = xSpeeds.flatMap { vx -> ySpeeds.map { vy -> Vector(vx, vy) } }
}

// region parsing
fun getRange(range: String): Pair<Int, Int> = range.split("=")[1].let {
    val (minValue, maxValue) = it.split("..").map(String::toInt)
    minValue to maxValue
}

fun String.toArea(): Area {
    val (_, values) = this.split(": ")
    val (xRange, yRange) = values.split(", ")

    val (minX, maxX) = getRange(xRange)
    val (minY, maxY) = getRange(yRange)

    return Area(Vector(minX, minY), Vector(maxX, maxY))
}
// endregion parsing


fun findPeak(input: Area, v0: Vector): Int? {
    var v = v0
    var p = Vector(0, 0)
    var maxY = 0

    while (p.x <= input.max.x && p.y >= input.min.y) {
        p += v
        v = v.updateVelocity()

        maxY = max(p.y, maxY)
        if (input.has(p)) {
            return maxY
        }
    }

    return null
}

fun part1(input: Area): Int =
    input.possibleVectors.maxOf { (x, y) ->
        findPeak(input, Vector(x, y)) ?: 0
    }

fun part2(input: Area): Int =
    input.possibleVectors.count { (x, y) ->
        findPeak(input, Vector(x, y)) != null
    }

fun main() {
    val sample = readInput(17, "sample").first().toArea()
    val input = readInput(17, "input").first().toArea()

    println("x: ${sample.xSpeeds}, y: ${sample.ySpeeds}")
    println("x: ${input.xSpeeds}, y: ${input.ySpeeds}")

    check(part1(sample) == 45)
    println(part1(input))

    check(part2(sample) == 112)
    println(part2(input))
}
