package year2021.day25

import product
import readInput

typealias Cucumbers = List<List<Char>>

fun printCucumbers(input: Cucumbers) {
    println(input.joinToString("\n") { it.joinToString("") })
}

fun isFreeLocation(input: Cucumbers, x: Int, y: Int): Boolean =
    input[y][x] == '.'

fun moveSomewhere(input: Cucumbers, facing: Char, dx: Int, dy: Int): Cucumbers {
    val output = input.toList().map { it.toMutableList() }

    val height = input.size
    val width = input.first().size
    for ((y, x) in product(input.indices, input.first().indices)) {
        if (input[y][x] != facing) {
            continue
        }

        val newX = (x + dx) % width
        val newY = (y + dy) % height
        if (isFreeLocation(input, newX, newY)) {
            output[y][x] = '.'
            output[newY][newX] = facing
        }
    }

    return output
}

fun moveEast(input: Cucumbers): Cucumbers = moveSomewhere(input, '>', 1, 0)
fun moveSouth(input: Cucumbers): Cucumbers = moveSomewhere(input, 'v', 0, 1)
fun move(input: Cucumbers): Cucumbers = moveSouth(moveEast(input))

fun part1(input: Cucumbers): Int {
    var counter = 0
    var previous: Cucumbers = emptyList()
    var current = input

    while (previous != current) {
        previous = current
        current = move(previous)
        counter++
    }

    return counter
}

fun part2(input: Cucumbers): String = "Merry Christmas I guess…"

fun main() {
    val smallerSample = readInput(25, "smaller_sample").map(String::toList)
    val sample = readInput(25, "sample").map(String::toList)
    val input = readInput(25, "input").map(String::toList)

    check(part1(sample) == 58)
    println(part1(input))

    println(part2(input))
}
