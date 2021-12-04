package day02

import readInput

enum class Command {
    FORWARD, DOWN, UP
}

fun main() {
    data class Position(val horizontal: Int = 0, val depth: Int = 0, val aim: Int = 0)
    data class Instruction(val type: Command, val value: Int)

    fun parseInstruction(instruction: String): Instruction {
        val splitLine = instruction.split(' ')
        check(splitLine.size == 2)

        val value = splitLine[1].toInt()
        when (splitLine[0]) {
            "forward" -> return Instruction(Command.FORWARD, value)
            "down" -> return Instruction(Command.DOWN, value)
            "up" -> return Instruction(Command.UP, value)
        }
        throw IllegalArgumentException("Cannot parse instruction")
    }

    fun toCommands(input: List<String>): List<Instruction> {
        return input.map { parseInstruction(it) }
    }

    fun part1(input: List<Instruction>): Int {
        val finalPosition = input.fold(Position()) { pos, instruction ->
            when (instruction.type) {
                Command.FORWARD -> pos.copy(horizontal = pos.horizontal + instruction.value)
                Command.UP -> pos.copy(depth = pos.depth - instruction.value)
                Command.DOWN -> pos.copy(depth = pos.depth + instruction.value)
            }
        }
        return finalPosition.horizontal * finalPosition.depth
    }

    fun part2(input: List<Instruction>): Int {
        val finalPosition = input.fold(Position()) { pos, instruction ->
            when (instruction.type) {
                Command.FORWARD -> pos.copy(
                    horizontal = pos.horizontal + instruction.value,
                    depth = pos.depth + instruction.value * pos.aim
                )
                Command.UP -> pos.copy(aim = pos.aim - instruction.value)
                Command.DOWN -> pos.copy(aim = pos.aim + instruction.value)
            }
        }
        return finalPosition.horizontal * finalPosition.depth
    }


    val testInput = toCommands(readInput(2, "test_input"))
    val input = toCommands(readInput(2, "input"))

    check(part1(testInput) == 150)
    println(part1(input))

    check(part2(testInput) == 900)
    println(part2(input))
}
