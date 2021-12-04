fun main() {
    fun part1(input: List<Int>): Int {
        return input.windowed(2).count { it[0] < it[1] }
    }

    fun part2(input: List<Int>): Int {
        return part1(input.windowed(3).map { it.sum() }.toList())
    }

    val testInput = readInputAsInts("Day01_test")
    val input = readInputAsInts("Day01")

    check(part1(testInput) == 7)
    println(part1(input))

    check(part2(testInput) == 5)
    println(part2(input))
}
