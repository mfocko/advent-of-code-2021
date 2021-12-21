package year2021.day21

import readInput

interface Die {
    val rolled: Int
    fun roll(): Iterable<Int>
}

data class RegularDie(var next: Int = 1, override var rolled: Int = 0) : Die {
    override fun roll(): Iterable<Int> {
        val rolls = listOf(next, (next + 1) % 100, (next + 2) % 100)

        rolled += 3
        next += 3
        next %= 100

        return rolls
    }
}

data class DiracDie(
    val rolls: List<Int> = (3..9).toList(),
    override val rolled: Int = -1
) : Die {
    override fun roll(): Iterable<Int> = rolls
}

data class Board(
    val players: MutableList<Int>,
    val winScore: Int,
    val die: Die,
    val scores: MutableList<Int> = mutableListOf(),
    private var nextPlayer: Int = 0,
) {
    init {
        if (scores.isEmpty()) {
            repeat(playersCount) { scores.add(0) }
        }
    }

    private val playersCount: Int
        get() = players.size

    val done: Boolean
        get() = winner != -1

    private val winner: Int
        get() = scores.indexOfFirst { it >= winScore }

    val losingScore: Int
        get() = scores.first { it < winScore }

    private fun playRound(roll: Int) {
        players[nextPlayer] = (players[nextPlayer] + roll) % 10
        scores[nextPlayer] += players[nextPlayer] + 1
        nextPlayer = (nextPlayer + 1) % playersCount
    }

    fun playRegular() = playRound(die.roll().sum())

    fun playDirac(): MutableList<ULong> {
        val winCount = mutableListOf<ULong>()
        repeat(players.size) { winCount.add(0UL) }

        playDirac(winCount, 1UL)

        return winCount
    }

    private val diracProbabilities = listOf(1, 3, 6, 7, 6, 3, 1).map(Int::toULong)
    private fun playDirac(winCount: MutableList<ULong>, factor: ULong) {
        if (done) {
            winCount[winner] += factor
            return
        }

        die.roll().zip(diracProbabilities).forEach { (roll, probability) ->
            val recursiveBoard = this.copy(players = players.toMutableList(), scores = scores.toMutableList())
            recursiveBoard.playRound(roll)
            recursiveBoard.playDirac(winCount, probability * factor)
        }
    }
}

fun List<String>.toBoard(): Board {
    val players = this.map { it.split(": ")[1].toInt() - 1 }.toMutableList()
    return Board(players, 1000, RegularDie())
}

fun List<String>.toDiracBoard(): Board {
    val players = this.map { it.split(": ")[1].toInt() - 1 }.toMutableList()
    return Board(players, 21, DiracDie())
}

fun part1(input: Board): Int {
    while (!input.done) {
        input.playRegular()
    }
    return input.losingScore * input.die.rolled
}

fun part2(input: Board): ULong = input.playDirac().maxOf { it }

fun main() {
    val sample = readInput(21, "sample")
    val input = readInput(21, "input")

    check(part1(sample.toBoard()) == 739785)
    println(part1(input.toBoard()))

    check(part2(sample.toDiracBoard()) == 444356092776315UL)
    println(part2(input.toDiracBoard()))
}
