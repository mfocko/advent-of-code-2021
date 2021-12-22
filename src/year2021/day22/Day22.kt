package year2021.day22

import product
import readInput

// region Global extensions
fun IntRange.intersect(other: IntRange): IntRange = maxOf(first, other.first)..minOf(last, other.last)
fun IntRange.size(): Long = if (isEmpty()) 0L else last - first + 1L
fun IntRange.splitBy(other: IntRange): Sequence<IntRange> =
    sequenceOf(first until other.first, other, other.last + 1..last)
        .map { it.intersect(this) }
        .filterNot(IntRange::isEmpty)
// endregion Global extensions

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

// region Cuboid
data class Cuboid(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    val allPoints: Iterable<Point>
        get() = xRange.flatMap { x -> yRange.flatMap { y -> zRange.map { z -> Point(x, y, z) } } }

    val size: Long
        get() = xRange.size() * yRange.size() * zRange.size()

    fun intersect(other: Cuboid): Cuboid = Cuboid(
        xRange = xRange.intersect(other.xRange),
        yRange = yRange.intersect(other.yRange),
        zRange = zRange.intersect(other.zRange),
    )

    private fun isEmpty(): Boolean = xRange.isEmpty() || yRange.isEmpty() || zRange.isEmpty()

    operator fun minus(other: Cuboid): Set<Cuboid> {
        val overlap = this.intersect(other)

        return if (overlap.isEmpty()) {
            setOf(this)
        } else if (overlap == this) {
            emptySet()
        } else {
            buildSet {
                for ((xs, ys, zs) in product(
                    xRange.splitBy(overlap.xRange),
                    yRange.splitBy(overlap.yRange),
                    zRange.splitBy(overlap.zRange),
                )) {
                    if (
                        xs.first !in overlap.xRange
                        || ys.first !in overlap.yRange
                        || zs.first !in overlap.zRange
                    ) {
                        add(Cuboid(xs, ys, zs))
                    }
                }
            }
        }
    }
}
// endregion Cuboid

// region Step
data class Step(val state: State, val cuboid: Cuboid) {
    val allPoints: Iterable<Point>
        get() = cuboid.allPoints

    fun crop(cuboid: Cuboid): Step = this.copy(cuboid = this.cuboid.intersect(cuboid))
}

fun String.toStep(): Step {
    val (state, regions) = this.split(" ")

    val (xRange, yRange, zRange) =
        regions.split(",")
            .map { it.split("=")[1].split("..").map(String::toInt) }
            .map { it[0]..it[1] }

    return Step(state.toState(), Cuboid(xRange, yRange, zRange))
}
// endregion Step

fun part1(input: List<Step>): Int = input.fold(mutableSetOf<Point>()) { turnedOn, step ->
    val croppedStep = step.crop(Cuboid(-50..50, -50..50, -50..50))
    when (step.state) {
        State.On -> turnedOn.addAll(croppedStep.allPoints)
        State.Off -> turnedOn.removeAll(croppedStep.allPoints)
    }

    turnedOn
}.size

// region Reactor
data class Reactor(val cuboids: List<Cuboid> = emptyList()) {
    fun execute(step: Step): Reactor {
        // remove current one from turned on
        val withoutStep = cuboids.flatMapTo(mutableListOf()) { it - step.cuboid }
        if (step.state == State.On) {
            withoutStep.add(step.cuboid)
        }

        return copy(cuboids = withoutStep)
    }
}
// endregion Reactor

fun part2(input: List<Step>): Long = input.fold(Reactor()) { reactor, step ->
    reactor.execute(step)
}.cuboids.sumOf { it.size }

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

