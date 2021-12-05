package day04

import readInput

class Bingo(description: List<String>) {
    private var nextDraw: Int = 0
    private var orderOfWin: MutableList<MutableList<MutableList<Int>>> = mutableListOf()

    private val draws: List<Int> = description[0].split(",").map { it.toInt() }
    private val boards: List<MutableList<MutableList<Int>>> = description.drop(1).chunked(6)
        .map { board ->
            board.drop(1)
                .map { row ->
                    row
                        .split(Regex("\\s+"))
                        .filter { it != "" }
                        .map { it.toInt() }
                        .toMutableList()
                }
                .toMutableList()
        }

    private fun mark(board: MutableList<MutableList<Int>>, number: Int) {
        board.indices
            .flatMap { i -> board.indices.map { j -> Pair(i, j) } }
            .filter { (i, j) -> board[i][j] == number }
            .forEach { (i, j) ->
                val before = isWinningBoard(board)
                board[i][j] = -1
                val after = isWinningBoard(board)

                if (before != after) {
                    orderOfWin.add(board)
                }
            }
    }

    private fun isWinningBoard(board: MutableList<MutableList<Int>>): Boolean = board.indices.any { i ->
        board[i].count { it == -1 } == board.size || board.indices.count { board[it][i] == -1 } == board.size
    }

    val winningBoardsCount: Int
        get() = boards.count { isWinningBoard(it) }
    val boardsCount: Int
        get() = boards.size
    val hasWinner: Boolean
        get() = boards.any { isWinningBoard(it) }

    fun draw() {
        val number = draws[nextDraw++]
        boards.forEach { mark(it, number) }
    }

    val lastDraw: Int
        get() {
            check(nextDraw > 0)
            return draws[nextDraw - 1]
        }

    fun sumOfLast(): Int = orderOfWin.last().sumOf { row -> row.filter { it != -1 }.sum() }
}

fun part1(input: Bingo): Int {
    while (!input.hasWinner) {
        input.draw()
    }
    return input.sumOfLast() * input.lastDraw
}

fun part2(input: Bingo): Int {
    while (input.winningBoardsCount < input.boardsCount) {
        input.draw()
    }
    return input.sumOfLast() * input.lastDraw
}

fun main() {
    val testInput = Bingo(readInput(4, "test_input"))
    val input = Bingo(readInput(4, "input"))

    check(part1(testInput) == 4512)
    println(part1(input))

    check(part2(testInput) == 1924)
    println(part2(input))
}
