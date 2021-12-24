package year2021.day23

import product
import readInput
import java.util.PriorityQueue
import kotlin.math.absoluteValue

// region Position
data class Position(val x: Int, val y: Int) {
    fun distance(other: Position): Int =
        if (y != 1 && other.y != 1) {
            val mid = Position(other.x, 1)
            this.distance(mid) + mid.distance(other)
        } else {
            (x - other.x).absoluteValue + (y - other.y).absoluteValue
        }
}
// endregion Position

// region Ampod
fun energyPerStep(type: Char): Int = when (type) {
    'A' -> 1
    'B' -> 10
    'C' -> 100
    'D' -> 1000
    else -> error("Invalid amphipod type")
}

fun targetRoom(type: Char): Int = when (type) {
    'A' -> 3
    'B' -> 5
    'C' -> 7
    'D' -> 9
    else -> error("Invalid amphipod type")
}

data class Ampod(val type: Char, val position: Position) {
    fun costTo(position: Position): Int = this.position.distance(position) * energyPerStep(type)

    val isInHallway: Boolean
        get() = position.y == 1

    val isInRoom: Boolean
        get() = position.y in 2..5 && position.x in (3..9 step 2)

    val isInCorrectRoom: Boolean
        get() = position.y in 2..5 && position.x == targetRoom(type)
}
// endregion Ampod

// region Diagram
data class Diagram(val ampods: Set<Ampod>, val maxY: Int = 3) {
    private val hallwaySpots =
        (1 until 12)
            .filter { (it < 3 || it > 9 || it in (4..8 step 2)) }
            .map { x -> Position(x, 1) }

    private val freeHallwaySpots: Sequence<Position>
        get() = hallwaySpots.asSequence().filter { !ampods.any { amphipod -> amphipod.position == it } }

    private val ampodsInHallway: Sequence<Ampod>
        get() = ampods.asSequence().filter(Ampod::isInHallway)

    private val ampodsInRooms: Sequence<Ampod>
        get() = ampods.asSequence().filter(Ampod::isInRoom)

    // TODO: Refactor
    private fun blocked(src: Position, dst: Position): Boolean {
        if (src.y == 1 && dst.y in 2..maxY) {
            return ampodsInHallway.any {
                (it.position.x > src.x && it.position.x <= dst.x) || (it.position.x < src.x && it.position.x >= dst.x)
            } || ampods.any {
                it.position.x == dst.x && it.position.y <= dst.y
            }
        } else if (src.y in 2..maxY && dst.y == 1) {
            return ampods.any {
                it.position.x == src.x && it.position.y < src.y
            } || ampodsInHallway.any {
                (it.position.x in src.x..dst.x) || (it.position.x in dst.x..src.x)
            }
        }

        return blocked(src, Position(dst.x, 1)) || blocked(Position(dst.x, 1), dst)
    }

    private fun moveAmpod(ampod: Ampod, position: Position): Diagram =
        this.copy(ampods = ampods - setOf(ampod) + setOf(ampod.copy(position = position)))

    private fun tryMoveFromHallway(ampod: Ampod): Pair<Diagram, Int>? {
        val targetX = targetRoom(ampod.type)

        if (ampodsInRooms.any { it.type != ampod.type && it.position.x == targetX }) {
            // there is still an ampod of different type in the room
            return null
        }

        val destination =
            Position(
                targetX,
                (2..maxY).reversed().first { y ->
                    !ampodsInRooms.any {
                        it.type == ampod.type && it.position == Position(
                            targetX, y
                        )
                    }
                }
            )
        if (blocked(ampod.position, destination)) {
            return null
        }

        return (moveAmpod(ampod, destination) to ampod.costTo(destination))
    }

    private fun tryMoveFromRoom(ampod: Ampod, destination: Position): Pair<Diagram, Int>? {
        if (blocked(ampod.position, destination)) {
            return null
        }
        return moveAmpod(ampod, destination) to ampod.costTo(destination)
    }

    fun moves(): Sequence<Pair<Diagram, Int>> = sequence {
        yieldAll(product(ampodsInRooms, freeHallwaySpots).mapNotNull { (amphipod, freeSpot) ->
            tryMoveFromRoom(amphipod, freeSpot)
        })
        yieldAll(ampodsInHallway.mapNotNull { tryMoveFromHallway(it) })
    }

    val goal: Diagram
        get() = this.copy(
            ampods = product('A'..'D', 2..maxY).map { (type, y) ->
                Ampod(
                    type,
                    Position(targetRoom(type), y)
                )
            }.toSet()
        )

    override fun toString(): String {
        fun charAt(x: Int, y: Int): Char =
            if (y > 2 && (x < 2 || x > 10))
                ' '
            else if (y == 0 || y == 4 || x == 0 || x == 12)
                '#'
            else if (y == 1 || x == 3 || x == 5 || x == 7 || x == 9)
                ampods.firstOrNull { it.position == Position(x, y) }?.type ?: '.'
            else
                '#'

        return (0 until 5).joinToString("\n") { y ->
            (0 until 13).map { x -> charAt(x, y) }.joinToString("")
        }
    }
}

fun List<String>.toDiagram(): Diagram = Diagram(
    this@toDiagram.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c ->
            if (c !in 'A'..'D') {
                null
            } else {
                Ampod(c, Position(x, y))
            }
        }
    }.toSet()
)
// endregion Diagram

// region solve
fun solve(state: Diagram, goal: Diagram): Int {
    val cost = mutableMapOf(state to 0)
    val visited = mutableSetOf<Diagram>()
    val queue = PriorityQueue<Pair<Diagram, Int>> { a, b ->
        a.second - b.second
    }

    queue.add(state to 0)

    while (queue.isNotEmpty()) {
        val (currentDiagram, currentCost) = queue.remove()
        if (visited.contains(currentDiagram)) {
            continue
        }

        if (currentDiagram == goal) {
            break
        }

        visited.add(currentDiagram)
        currentDiagram.moves().forEach { (next, nextCost) ->
            val alternativeCost = currentCost + nextCost
            if (alternativeCost < cost.getOrDefault(next, Int.MAX_VALUE)) {
                cost[next] = alternativeCost
                queue.add(next to alternativeCost)
            }
        }
    }

    return cost[goal]!!
}
// endregion solve

fun part1(input: Diagram): Int = solve(input, input.goal)
fun part2(input: Diagram): Int = input.copy(maxY = 5).let { newInput -> solve(newInput, newInput.goal) }

fun main() {
    val sample = readInput(23, "sample").toDiagram()
    val input = readInput(23, "input").toDiagram()

    check(part1(sample) == 12521)
    println("[PASS] Part #1 check")
    println(part1(input))

    val sample2 = readInput(23, "sample2").toDiagram()
    val input2 = readInput(23, "input2").toDiagram()

    check(part2(sample2) == 44169)
    println("[PASS] Part #2 check")
    println(part2(input2))
}
