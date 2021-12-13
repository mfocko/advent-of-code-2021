package year2021.day13

import readInput

enum class Along {
    X, Y
}

data class Point(val x: Int, val y: Int)

data class Instruction(val along: Along, val coordinate: Int) {
    private fun isOver(point: Point): Boolean = when (along) {
        Along.X -> point.x > coordinate
        Along.Y -> point.y > coordinate
    }

    private fun flip(point: Point): Point = when (along) {
        Along.X -> Point(2 * coordinate - point.x, point.y)
        Along.Y -> Point(point.x, 2 * coordinate - point.y)
    }

    fun execute(points: Set<Point>): Set<Point> {
        val pointsOverFold = points.filter { isOver(it) }
        val pointsFlipped = pointsOverFold.map { flip(it) }
        return points - pointsOverFold.toSet() + pointsFlipped.toSet()
    }
}

data class Origami(private var points: Set<Point>, val instructions: List<Instruction>, private var i: Int = 0) {
    val visiblePoints: Int
        get() = points.size

    val instructionsLeft: Boolean
        get() = i < instructions.size

    fun nextInstruction(): Origami {
        if (i >= instructions.size) {
            return this
        }

        points = instructions[i++].execute(points)
        return this
    }

    override fun toString(): String =
        (0..points.maxOf { it.y }).joinToString("\n") { y ->
            (0..points.maxOf { it.x }).joinToString("") { x -> if (points.contains(Point(x, y))) "#" else " " }
        }
}

fun String.toPoint(): Point {
    val (x, y) = this.split(",").map(String::toInt)
    return Point(x, y)
}

fun String.toInstruction(): Instruction {
    val (_, _, description) = this.split(" ")
    val (axis, coordinate) = description.split("=")
    return Instruction(if (axis == "x") Along.X else Along.Y, coordinate.toInt())
}

fun readOrigami(input: List<String>): Origami =
    input
        .joinToString("\n")
        .split("\n\n")
        .map { it.split("\n") }
        .let { (points, instructions) ->
            Origami(
                points.map(String::toPoint).toSet(),
                instructions.map(String::toInstruction)
            )
        }

fun part1(input: List<String>): Int =
    readOrigami(input).nextInstruction().visiblePoints

fun part2(input: List<String>): String {
    val origami = readOrigami(input)
    while (origami.instructionsLeft) {
        origami.nextInstruction()
    }
    return origami.toString()
}

fun main() {
    val sample = readInput(13, "sample")
    val input = readInput(13, "input")

    check(part1(sample) == 17)
    println(part1(input))

    println(part2(sample))
    println()
    println(part2(input))
}
