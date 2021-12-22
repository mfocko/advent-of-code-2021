package year2021.day22

import product
import readInput

// region Global extensions
fun IntRange.intersect(other: IntRange): IntRange =
    maxOf(first, other.first)..minOf(last, other.last)

val IntRange.size: Long
    get() = if (isEmpty()) 0L else last - first + 1L

fun IntRange.splitBy(other: IntRange): Sequence<IntRange> =
    sequenceOf(
        first until other.first, other, other.last + 1..last
    )
        .map { it.intersect(this) }
        .filterNot(IntRange::isEmpty)

val IntRange.Companion.MAX_RANGE: IntRange
    get() = Int.MIN_VALUE..Int.MAX_VALUE
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

// region Cuboid
data class Cuboid(
    val xRange: IntRange, val yRange: IntRange, val zRange: IntRange
) {
    val size: Long
        get() = xRange.size * yRange.size * zRange.size

    fun intersect(other: Cuboid): Cuboid = Cuboid(
        xRange = xRange.intersect(other.xRange),
        yRange = yRange.intersect(other.yRange),
        zRange = zRange.intersect(other.zRange),
    )

    private fun intersectedBy(other: Cuboid): Boolean =
        (xRange.first in other.xRange
                && yRange.first in other.yRange
                && zRange.first in other.zRange)

    fun isEmpty(): Boolean =
        xRange.isEmpty() || yRange.isEmpty() || zRange.isEmpty()

    operator fun minus(other: Cuboid): Set<Cuboid> {
        val overlap = this.intersect(other)

        return if (overlap.isEmpty()) {
            setOf(this)
        } else if (overlap == this) {
            emptySet()
        } else {
            product(
                xRange.splitBy(overlap.xRange),
                yRange.splitBy(overlap.yRange),
                zRange.splitBy(overlap.zRange),
            )
                .map { (xs, ys, zs) -> Cuboid(xs, ys, zs) }
                .filter { !it.intersectedBy(overlap) }
                .toSet()
        }
    }

    companion object {
        val MAX_CUBOID =
            Cuboid(IntRange.MAX_RANGE, IntRange.MAX_RANGE, IntRange.MAX_RANGE)
    }
}
// endregion Cuboid

// region Step
data class Step(val state: State, val cuboid: Cuboid)

fun String.toStep(): Step {
    val (state, regions) = this.split(" ")

    val (xRange, yRange, zRange) =
        regions.split(",")
            .map { it.split("=")[1].split("..").map(String::toInt) }
            .map { it[0]..it[1] }

    return Step(state.toState(), Cuboid(xRange, yRange, zRange))
}
// endregion Step

// region Reactor
data class Reactor(
    val interestingCuboid: Cuboid = Cuboid.MAX_CUBOID,
    val cuboids: List<Cuboid> = emptyList()
) {
    fun execute(step: Step): Reactor {
        val affectedCuboid = interestingCuboid.intersect(step.cuboid)
        if (affectedCuboid.isEmpty()) {
            return this
        }

        // remove current one from turned on
        val withoutStep = cuboids.flatMapTo(mutableListOf()) { it - affectedCuboid }
        if (step.state == State.On) {
            withoutStep.add(affectedCuboid)
        }

        return copy(cuboids = withoutStep)
    }
}
// endregion Reactor

val INIT_RANGE = -50..50
fun part1(input: List<Step>): Long =
    input.fold(Reactor(Cuboid(INIT_RANGE, INIT_RANGE, INIT_RANGE))) { reactor, step ->
        reactor.execute(step)
    }.cuboids.sumOf { it.size }

fun part2(input: List<Step>): Long =
    input.fold(Reactor()) { reactor, step ->
        reactor.execute(step)
    }.cuboids.sumOf { it.size }

fun main() {
    val smallerSample = readInput(22, "smaller_sample").map(String::toStep)
    val sample = readInput(22, "sample").map(String::toStep)
    val input = readInput(22, "input").map(String::toStep)

    check(part1(smallerSample) == 39L)
    check(part1(sample) == 590784L)
    println(part1(input))

    val rebootSample = readInput(22, "reboot_sample").map(String::toStep)

    check(part2(rebootSample) == 2758514936282235L)
    println(part2(input))
}

