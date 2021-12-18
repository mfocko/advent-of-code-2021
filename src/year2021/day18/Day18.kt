package year2021.day18

import readInput

enum class SnailfishNumberType {
    Regular,
    Pair
}

abstract class SpecialNumber : Cloneable {
    public abstract override fun clone(): SpecialNumber
    abstract fun explode(path: MutableList<SpecialNumber>): Pair<Boolean, SpecialNumber>
    abstract fun split(path: MutableList<SpecialNumber>): Pair<Boolean, SpecialNumber>

    abstract val type: SnailfishNumberType
    abstract val magnitude: Long

    operator fun plus(other: SpecialNumber): SpecialNumber = reduce(PairNumber(this.clone(), other.clone()))
}

data class RegularNumber(var number: Long) : SpecialNumber() {
    override fun clone(): SpecialNumber = RegularNumber(number)
    override fun explode(path: MutableList<SpecialNumber>): Pair<Boolean, SpecialNumber> = false to this
    override fun split(path: MutableList<SpecialNumber>): Pair<Boolean, SpecialNumber> =
        if (number < 10)
            (false to this)
        else
            (true to PairNumber(
                RegularNumber(number / 2), RegularNumber(number - number / 2)
            ))

    override val type: SnailfishNumberType
        get() = SnailfishNumberType.Regular

    override val magnitude: Long
        get() = number

    override fun toString(): String = "$number"
}

data class PairNumber(var left: SpecialNumber, var right: SpecialNumber) : SpecialNumber() {
    override fun clone(): SpecialNumber = PairNumber(left.clone(), right.clone())

    private fun tryReduce(
        operation: (SpecialNumber, MutableList<SpecialNumber>) -> Pair<Boolean, SpecialNumber>,
        path: MutableList<SpecialNumber>
    ): Pair<Boolean, SpecialNumber> {
        path.add(this)

        val (reducedLeft, left) = operation(left, path)
        this.left = left
        if (reducedLeft) {
            path.removeLast()
            return true to this
        }

        val (reducedRight, right) = operation(right, path)
        this.right = right

        path.removeLast()
        return reducedRight to this
    }

    override fun explode(path: MutableList<SpecialNumber>): Pair<Boolean, SpecialNumber> {
        path.add(this)

        if (path.size > 4 && left.type == SnailfishNumberType.Regular && right.type == SnailfishNumberType.Regular) {
            val previous = predecessor(path)
            if (previous != null) {
                previous.number += left.magnitude
            }

            val next = successor(path)
            if (next != null) {
                next.number += right.magnitude
            }

            path.removeLast()
            return true to RegularNumber(0)
        }

        path.removeLast()
        return tryReduce(SpecialNumber::explode, path)
    }

    override fun split(path: MutableList<SpecialNumber>): Pair<Boolean, SpecialNumber> =
        tryReduce(SpecialNumber::split, path)

    override val type: SnailfishNumberType
        get() = SnailfishNumberType.Pair

    override val magnitude: Long
        get() = 3 * left.magnitude + 2 * right.magnitude

    override fun toString(): String = "[$left,$right]"
}

fun String.toSnailfishNumber(): SpecialNumber {
    fun parseNumber(fromIndex: Int): Pair<Int, SpecialNumber> {
        if (this[fromIndex] != '[') {
            val toIndex =
                (fromIndex until this.length).firstOrNull { this[it] == ',' || this[it] == ']' } ?: this.length

            return toIndex to RegularNumber(this.slice(fromIndex until toIndex).toLong())
        }

        check(this[fromIndex] == '[')

        val (comma, left) = parseNumber(fromIndex + 1)
        check(this[comma] == ',')

        val (toIndex, right) = parseNumber(comma + 1)
        check(this[toIndex] == ']')

        return toIndex + 1 to PairNumber(left, right)
    }
    val (index, number) = parseNumber(0)
    check(index == this.length)
    return number
}

fun predecessor(path: List<SpecialNumber>): RegularNumber? {
    var i = path.size - 1

    while (i > 0) {
        if (path[i] === (path[i - 1] as PairNumber).right) {
            var node = (path[i - 1] as PairNumber).left

            while (node !is RegularNumber) {
                node = (node as PairNumber).right
            }
            return node
        }
        i--
    }

    return null
}

fun successor(path: List<SpecialNumber>): RegularNumber? {
    var i = path.size - 1

    while (i > 0) {
        if (path[i] === (path[i - 1] as PairNumber).left) {
            var node = (path[i - 1] as PairNumber).right

            while (node !is RegularNumber) {
                node = (node as PairNumber).left
            }
            return node
        }
        i--
    }

    return null
}

fun reduce(number: SpecialNumber): SpecialNumber {
    var newNumber = number
    val stack = mutableListOf<SpecialNumber>()

    while (true) {
        val (hasExploded, afterExplode) = newNumber.explode(stack)
        check(stack.isEmpty())
        if (hasExploded) {
            newNumber = afterExplode
            continue
        }

        val (hasSplit, afterSplit) = newNumber.split(stack)
        check(stack.isEmpty())
        if (hasSplit) {
            newNumber = afterSplit
            continue
        }

        break
    }

    return newNumber
}

fun asSFNumber(number: Long): SpecialNumber = RegularNumber(number)
fun asSFNumber(left: Long, right: Long): SpecialNumber = PairNumber(RegularNumber(left), RegularNumber(right))
fun asSFNumber(left: SpecialNumber, right: Long): SpecialNumber = PairNumber(left, RegularNumber(right))
fun asSFNumber(left: Long, right: SpecialNumber): SpecialNumber = PairNumber(RegularNumber(left), right)
fun asSFNumber(left: SpecialNumber, right: SpecialNumber): SpecialNumber = PairNumber(left, right)

fun part1(input: List<String>): Long =
    input.map { it.toSnailfishNumber() }
        .reduce(SpecialNumber::plus)
        .magnitude

fun part2(input: List<String>): Long =
    input.map(String::toSnailfishNumber).let { numbers ->
        numbers.indices
            .flatMap { i -> numbers.indices.map { j -> i to j } }
            .filter { (i, j) -> i != j }.maxOf { (i, j) ->
                (numbers[i] + numbers[j]).magnitude
            }
    }

fun main() {
    val sample = readInput(18, "sample")
    val input = readInput(18, "input")

    check(part1(sample) == 4140L)
    println(part1(input))

    check(part2(sample) == 3993L)
    println(part2(input))
}
