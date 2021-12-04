fun main() {
    fun toBits(input: List<String>): List<List<Int>> {
        return input.map { row ->
            row.map { if (it == '1') 1 else 0 }
        }
    }

    fun part1(input: List<List<Int>>): Int {
        var gamma = 0
        var epsilon = 0

        for (i in input[0].indices) {
            gamma = gamma.shl(1)
            epsilon = epsilon.shl(1)

            val ones = input.indices.map { input[it][i] }.sum()

            if (2 * ones > input.size) {
                gamma++
            } else {
                epsilon++
            }
        }

        return epsilon * gamma
    }

    fun part2(input: List<List<Int>>): Int {
        fun part2Helper(predicate: (Int, Int) -> Boolean): Int {
            var wanted = input.indices.toList()
            var i = 0

            while (wanted.size > 1) {
                val ones = wanted.sumOf { input[it][i] }
                val wantedBit = if (predicate(ones, wanted.size)) 1 else 0

                wanted = wanted.filter { input[it][i] == wantedBit }.toList()
                i++
            }

            return input[wanted[0]].fold(0) { n, d -> 2 * n + d }
        }

        val oxygenGeneratorRating = part2Helper { ones, length -> 2 * ones >= length }
        val co2ScrubberRating = part2Helper { ones, length -> 2 * ones < length }

        return oxygenGeneratorRating * co2ScrubberRating
    }


    val testInput = toBits(readInput("Day03_test"))
    val input = toBits(readInput("Day03"))

    check(part1(testInput) == 198)
    println(part1(input))

    check(part2(testInput) == 230)
    println(part2(input))
}
