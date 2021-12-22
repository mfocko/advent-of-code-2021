package year2021.day22

import readInput

// region global extensions
fun IntRange.intersect(other: IntRange): IntRange = maxOf(first, other.first)..minOf(last, other.last)
// endregion global extensions

// region State
enum class State {
    On, Off
}

fun String.toState(): State = when (this) {
    "on" -> State.On
    "off" -> State.Off
    else -> error("Invalid state given")
}
// endregion State

// region Point
data class Point(val x: Int, val y: Int, val z: Int) {
    fun within(xRange: IntRange, yRange: IntRange, zRange: IntRange): Boolean =
        x in xRange && y in yRange && z in zRange
}
// endregion Point

// region Cube
data class Cube(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    val allPoints: Iterable<Point>
        get() = xRange.flatMap { x -> yRange.flatMap { y -> zRange.map { z -> Point(x, y, z) } } }

    fun intersect(other: Cube): Cube = Cube(
        xRange = xRange.intersect(other.xRange),
        yRange = yRange.intersect(other.yRange),
        zRange = zRange.intersect(other.zRange),
    )
}
// endregion Cube

// region Step
data class Step(val state: State, val cube: Cube) {
    val allPoints: Iterable<Point>
        get() = cube.allPoints

    fun crop(cube: Cube): Step = this.copy(cube = this.cube.intersect(cube))
}

fun String.toStep(): Step {
    val (state, regions) = this.split(" ")

    val (xRange, yRange, zRange) =
        regions.split(",")
            .map { it.split("=")[1].split("..").map(String::toInt) }
            .map { it[0]..it[1] }

    return Step(state.toState(), Cube(xRange, yRange, zRange))
}
// endregion Step

fun part1(input: List<Step>): Int = input.fold(mutableSetOf<Point>()) { turnedOn, step ->
    val croppedStep = step.crop(Cube(-50..50, -50..50, -50..50))
    when (step.state) {
        State.On -> turnedOn.addAll(croppedStep.allPoints)
        State.Off -> turnedOn.removeAll(croppedStep.allPoints)
    }

    turnedOn
}.size

fun part2(input: List<Step>): Long = input.fold(mutableSetOf<Triple<Int, Int, Int>>()) { turnedOn, step ->
    when (step.state) {
        State.On -> turnedOn.addAll(step.allPoints.map { Triple(it.x, it.y, it.z) })
        State.Off -> turnedOn.removeAll(step.allPoints.map { Triple(it.x, it.y, it.z) })
    }

    turnedOn
}.size.toLong()

fun main() {
    val smallerSample = readInput(22, "smaller_sample").map(String::toStep)
    val sample = readInput(22, "sample").map(String::toStep)
    val input = readInput(22, "input").map(String::toStep)

    check(part1(smallerSample) == 39)
    check(part1(sample) == 590784)
    println(part1(input))

    val rebootSample = readInput(22, "reboot_sample").map(String::toStep)

    check(part2(rebootSample) == 2758514936282235L)
    println(part2(input))
}

