package year2021.day15

import readInput
import java.util.*

data class Coordinate(val y: Int, val x: Int) {
    fun adjacent(): Iterable<Coordinate> =
        listOf(
            Coordinate(y - 1, x),
            Coordinate(y + 1, x),
            Coordinate(y, x + 1),
            Coordinate(y, x - 1)
        )
}

open class Dijkstra(
    private val input: List<List<Int>>
) {
    private val queue: PriorityQueue<Pair<Int, Coordinate>> =
        PriorityQueue<Pair<Int, Coordinate>> { a, b ->
            a.first - b.first
        }
    private val distances = mutableMapOf<Coordinate, Int>()
    private val processed = mutableSetOf<Coordinate>()

    fun getDistance(coordinate: Coordinate): Int =
        distances[coordinate] ?: Int.MAX_VALUE

    open fun getCost(coordinate: Coordinate): Int =
        input[coordinate.y][coordinate.x]

    private fun relax(distance: Int, neighbour: Coordinate): Boolean {
        val alternative = distance + getCost(neighbour)
        if (alternative >= getDistance(neighbour)) {
            return false
        }

        distances[neighbour] = alternative
        queue.add(Pair(alternative, neighbour))
        return true
    }

    open fun neighbours(coordinate: Coordinate): Iterable<Coordinate> =
        coordinate.adjacent().filter { (y, x) ->
            (y >= 0 && y < input.size) && (x >= 0 && x < input[y].size)
        }

    fun run(start: Coordinate, end: Coordinate): Dijkstra {
        queue.add(Pair(0, start))
        distances[start] = 0

        while (queue.isNotEmpty()) {
            val (distance, coordinate) = queue.remove()!!
            if (processed.contains(coordinate)) {
                continue
            }
            processed.add(coordinate)

            if (coordinate == end) {
                return this
            }

            neighbours(coordinate).forEach { relax(distance, it) }
        }

        return this
    }
}

fun part1(input: List<List<Int>>): Int =
    Coordinate(input.size - 1, input.last().size - 1).let { end ->
        Dijkstra(input)
            .run(
                Coordinate(0, 0),
                end
            ).getDistance(end)
    }

class DijkstraOnExtended(private val input: List<List<Int>>) : Dijkstra(input) {
    override fun neighbours(coordinate: Coordinate): Iterable<Coordinate> =
        coordinate.adjacent().filter { (y, x) ->
            (y >= 0 && y < 5 * input.size) && (x >= 0 && x < 5 * input[0].size)
        }

    override fun getCost(coordinate: Coordinate): Int {
        val (y, x) = coordinate

        val added = y / input.size + x / input[0].size
        val riskLevel = input[y % input.size][x % input[0].size] + added

        return if (riskLevel > 9) riskLevel - 9 else riskLevel
    }
}

fun part2(input: List<List<Int>>): Int =
    Coordinate(5 * input.size - 1, 5 * input.last().size - 1).let { end ->
        DijkstraOnExtended(input)
            .run(
                Coordinate(0, 0),
                end
            ).getDistance(end)
    }

fun main() {
    val sample = readInput(15, "sample").map { row -> row.map { it.digitToInt() } }
    val input = readInput(15, "input").map { row -> row.map { it.digitToInt() } }

    check(part1(sample) == 40)
    println(part1(input))

    check(part2(sample) == 315)
    println(part2(input))
}
