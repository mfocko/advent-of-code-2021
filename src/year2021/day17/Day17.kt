package year2021.day17

import readInput
import kotlin.math.max
import kotlin.math.sign

data class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector): Vector = Vector(x + other.x, y + other.y)
}

data class Area(val min: Vector, val max: Vector) {
    fun has(z: Vector): Boolean = (z.x in min.x..max.x) && (z.y in min.y..max.y)
}

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

fun findPeak(input: Area, v0: Vector): Int? {
    fun updateVelocity(v: Vector): Vector = Vector(v.x - v.x.sign, v.y - 1)

    var v = v0
    var p = Vector(0, 0)

    var maxY = 0
    var hitTarget = false

    while (p.x <= input.max.x && p.y >= input.min.y) {
        p += v
        v = updateVelocity(v)

        hitTarget = hitTarget || input.has(p)
        maxY = max(p.y, maxY)
    }

    return if (hitTarget) maxY else null
}

fun part1(input: Area): Int =
    (0..input.max.x)
        .flatMap { x -> (input.min.y..-input.min.y).map { y -> x to y } }
        .maxOf { (x, y) ->
            findPeak(input, Vector(x, y)) ?: 0
        }

// x = v_0x * t + 1/2 * a_x * t^2
// y = v_0y * t + 1/2 * -1 * t^2
// v_fy = v_0y + a_y * t => t_up = v_0y / a_y

fun part2(input: Area): Int =
    (0..input.max.x)
        .flatMap { x -> (input.min.y..-input.min.y).map { y -> x to y } }
        .count { (x, y) ->
            findPeak(input, Vector(x, y)) != null
        }

fun main() {
    val sample = readInput(17, "sample").first().toArea()
    val input = readInput(17, "input").first().toArea()

    println("x: 0..${sample.max.x}, y: ${sample.min.y}..${-sample.min.y}")
    println("x: 0..${input.max.x}, y: ${input.min.y}..${-input.min.y}")

    check(part1(sample) == 45)
    println(part1(input))

    check(part2(sample) == 112)
    println(part2(input))
}
