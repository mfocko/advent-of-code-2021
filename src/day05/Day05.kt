package day05

import readInput

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
    operator fun times(other: Int): Point = Point(other * x, other * y)
}

data class Vector(val from: Point, val to: Point) {
    val horizontal: Boolean
        get() = from.y == to.y

    val vertical: Boolean
        get() = from.x == to.x

    val horizontalOrVertical: Boolean
        get() = horizontal || vertical

    private val xs: Iterable<Int>
        get() = if (from.x <= to.x) (from.x..to.x) else (to.x..from.x).reversed()

    private val ys: Iterable<Int>
        get() = if (from.y <= to.y) (from.y..to.y) else (to.y..from.y).reversed()

    val points: Iterable<Point>
        get() = if (horizontalOrVertical) {
            xs.flatMap { x -> ys.map { y -> Point(x, y) } }
        } else {
            xs.zip(ys).map { (x, y) -> Point(x, y) }
        }
}

fun readPoint(input: String): Point {
    val (x, y) = input.split(",").map { it.toInt() }
    return Point(x, y)
}

fun readVector(input: String): Vector {
    val (from, to) = input.split(" -> ")
    return Vector(readPoint(from), readPoint(to))
}

fun part1(input: List<Vector>): Int {
    var points: MutableMap<Point, Int> = mutableMapOf()

    input.filter { it.horizontalOrVertical }
        .flatMap { it.points }
        .forEach { points[it] = 1 + points.getOrDefault(it, 0) }

    return points.count { (_, count) -> count >= 2 }
}

fun part2(input: List<Vector>): Int {
    var points: MutableMap<Point, Int> = mutableMapOf()

    input.flatMap { it.points }
        .forEach { points[it] = 1 + points.getOrDefault(it, 0) }

    return points.count { (_, count) -> count >= 2 }
}

fun main() {
    val testInput = readInput(5, "test_input").map { readVector(it) }
    val input = readInput(5, "input").map { readVector(it) }

    check(part1(testInput) == 5)
    println(part1(input))

    check(part2(testInput) == 12)
    println(part2(input))
}
