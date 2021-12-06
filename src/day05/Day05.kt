package day05

import readInput

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
    operator fun times(other: Int): Point = Point(other * x, other * y)
}

fun getRange(from: Int, to: Int): Iterable<Int> = if (from <= to) {
    from..to
} else {
    (to..from).reversed()
}

data class Vector(val from: Point, val to: Point) {
    val horizontal: Boolean
        get() = from.y == to.y

    val vertical: Boolean
        get() = from.x == to.x

    val horizontalOrVertical: Boolean
        get() = horizontal || vertical

    private val xs: Iterable<Int>
        get() = getRange(from.x, to.x)

    private val ys: Iterable<Int>
        get() = getRange(from.y, to.y)

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

fun findOverlaps(input: List<Vector>): Int {
    val points: MutableMap<Point, Int> = mutableMapOf()
    input
        .flatMap { it.points }
        .forEach { points[it] = 1 + points.getOrDefault(it, 0) }
    return points.count { (_, count) -> count >= 2 }
}

fun main() {
    val testInput = readInput(5, "test_input").map { readVector(it) }
    val input = readInput(5, "input").map { readVector(it) }

    check(findOverlaps(testInput.filter { it.horizontalOrVertical }) == 5)
    println(findOverlaps(input.filter { it.horizontalOrVertical }))

    check(findOverlaps(testInput) == 12)
    println(findOverlaps(input))
}
