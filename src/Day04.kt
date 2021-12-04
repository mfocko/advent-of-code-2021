fun main() {
    class Bingo(description: List<String>) {
        private val draws: List<Int> = description[0].split(",").map { it.toInt() }
        private var nextDraw: Int = 0
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
        private var orderOfWin: MutableList<MutableList<MutableList<Int>>> = mutableListOf()

        fun mark(board: MutableList<MutableList<Int>>, number: Int) {
            for (i in board.indices) {
                for (j in board[i].indices) {
                    if (board[i][j] == number) {
                        val before = isWinningBoard(board)
                        board[i][j] = -1
                        val after = isWinningBoard(board)

                        if (before != after) {
                            orderOfWin.add(board)
                        }
                    }
                }
            }
        }

        fun isWinningBoard(board: MutableList<MutableList<Int>>): Boolean {
            for (i in board.indices) {
                val inRow = board[i].count { it == -1 }
                val inColumn = (board.indices).count { board[it][i] == -1 }

                if (inRow == board.size || inColumn == board.size) {
                    return true
                }
            }

            return false
        }

        fun winningBoardsCount(): Int {
            var counter = 0

            for (board in boards) {
                if (isWinningBoard(board)) {
                    counter++
                }
            }

            return counter
        }

        fun boardsCount(): Int {
            return boards.size
        }

        val hasWinner: Boolean
            get() {
                for (board in boards) {
                    if (isWinningBoard(board)) {
                        return true
                    }
                }

                return false
            }

        fun draw() {
            val number = draws[nextDraw++]

            for (board in boards) {
                mark(board, number)
            }
        }

        fun sumOfUnmarked(): Int {
            for (board in boards) {
                if (!isWinningBoard(board)) {
                    continue
                }
                return board.sumOf { row -> row.filter { it != -1 }.sum() }
            }
            return -1
        }

        fun sumOfLast(): Int {
            return orderOfWin.last().sumOf { row -> row.filter { it != -1 }.sum() }
        }

        fun lastDraw(): Int {
            check(nextDraw > 0)
            return draws[nextDraw - 1]
        }
    }

    fun part1(input: Bingo): Int {
        while (!input.hasWinner) {
            input.draw()
        }

        return input.sumOfUnmarked() * input.lastDraw()
    }

    fun part2(input: Bingo): Int {
        while (input.winningBoardsCount() < input.boardsCount()) {
            input.draw()
        }

        return input.sumOfLast() * input.lastDraw()
    }

    val testInput = Bingo(readInput("Day04_test"))
    val input = Bingo(readInput("Day04"))

    check(part1(testInput) == 4512)
    println(part1(input))

    check(part2(testInput) == 1924)
    println(part2(input))
}
