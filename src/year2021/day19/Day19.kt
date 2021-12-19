package year2021.day19

import readInputAsString
import java.time.Instant
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt

data class Vector(val x: Int, val y: Int, val z: Int) {
    fun magnitude(): Double = sqrt((x * x + y * y + z * z).toDouble())
    fun translate(by: Vector): Vector = this - by
    fun rotate(u: Vector, v: Vector, w: Vector): Vector =
        Vector(
            u.x * x + v.x * y + w.x * z,
            u.y * x + v.y * y + w.y * z,
            u.z * x + v.z * y + w.z * z,
        )

    operator fun plus(v: Vector): Vector = Vector(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vector): Vector = Vector(x - v.x, y - v.y, z - v.z)
    operator fun unaryMinus(): Vector = Vector(-x, -y, -z)
    operator fun times(c: Int): Vector = Vector(c * x, c * y, c * z)
}

data class Scanner(val beacons: Set<Vector>)

// region parsing
fun String.toVector() =
    this.split(",").map(String::toInt).let { (x, y, z) ->
        Vector(x, y, z)
    }

fun String.toScanner() =
    Scanner(this.split("\n").drop(1).map(String::toVector).toSet())
// endregion

// region constants
val TRIGONOMETRY: Set<Pair<Int, Int>> =
    listOf(0, 90, 180, 270)
        .map { Math.toRadians(it.toDouble()) }
        .map { sin(it).toInt() to cos(it).toInt() }
        .toSet()

val ROTATIONS: Set<Triple<Vector, Vector, Vector>> =
    TRIGONOMETRY
        .flatMap { alpha -> TRIGONOMETRY.flatMap { beta -> TRIGONOMETRY.map { gamma -> Triple(alpha, beta, gamma) } } }
        .map { (a, b, g) ->
            val (sinA, cosA) = a
            val (sinB, cosB) = b
            val (sinG, cosG) = g

            Triple(
                Vector(cosA * cosB, sinA * cosB, -sinB),
                Vector(cosA * sinB * sinG - sinA * cosG, sinA * sinB * sinG + cosA * cosG, cosB * sinG),
                Vector(cosA * sinB * cosG + sinA * sinG, sinA * sinB * cosG - cosA * sinG, cosB * cosG)
            )
        }
        .toSet()
// endregion

fun checkOverlap(beacons: Set<Vector>, scanner: Scanner): Pair<Vector, List<Vector>>? {
    ROTATIONS.forEach { (u, v, w) ->
        val rotatedBeacons = scanner.beacons.map { it.rotate(u, v, w) }

        beacons.forEach { anchor -> // anchor from already located beacons
            val translateWithRegardsToAnchor = beacons.map { it.translate(anchor) }.toSet()
            rotatedBeacons.forEach { rotatedAnchor ->
                val translateWithRegardsToRotatedAnchor = rotatedBeacons.map { it.translate(rotatedAnchor) }.toSet()

                val intersect = translateWithRegardsToAnchor.intersect(translateWithRegardsToRotatedAnchor)
                if (intersect.size >= 12) {
                    val scannerPosition = rotatedAnchor - anchor
                    val translatedBeacons = rotatedBeacons.map { it - rotatedAnchor + anchor }
                    return scannerPosition to translatedBeacons
                }
            }
        }
    }

    return null
}

fun locate(scanners: List<Scanner>): Pair<Set<Vector>, Set<Vector>> {
    val beacons = mutableSetOf<Vector>()
    val scannerPositions = mutableSetOf<Vector>()

    // add first scanner that is used as anchor
    beacons.addAll(scanners.first().beacons)

    val remainingScanners = scanners.drop(1).toMutableList()
    while (remainingScanners.isNotEmpty()) {
        for (i in remainingScanners.indices.reversed()) {
            val (scannerPosition, translatedBeacons) = checkOverlap(beacons, remainingScanners[i]) ?: continue

            beacons.addAll(translatedBeacons)
            scannerPositions.add(scannerPosition)
            remainingScanners.removeAt(i)
            println("Done: ${scanners.size - remainingScanners.size}/${scanners.size}")
        }
    }

    return scannerPositions to beacons
}

fun part1(input: Set<Vector>): Int = input.size

fun part2(input: Set<Vector>): Int =
    input.maxOf { a ->
        input.maxOf { b ->
            (a.x - b.x).absoluteValue + (a.y - b.y).absoluteValue + (a.z - b.z).absoluteValue
        }
    }

fun main() {
    val sample =
        readInputAsString(19, "sample").split("\n\n").map(String::toScanner)
    val input =
        readInputAsString(19, "input").split("\n\n").map(String::toScanner)

    println(Date.from(Instant.now()))
    val (sampleScanners, sampleBeacons) = locate(sample)
    check(part1(sampleBeacons) == 79)
    check(part2(sampleScanners) == 3621)

    val (inputScanners, inputBeacons) = locate(input)
    println(part1(inputBeacons))
    println(part2(inputScanners))
    println(Date.from(Instant.now()))
}
