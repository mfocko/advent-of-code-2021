package year2021.day14

import readInput

typealias PolymerPair = String
typealias PolymerRules = Map<PolymerPair, Char>
typealias PolymerTemplate = Map<PolymerPair, Long>

data class Polymer(
    val rules: PolymerRules,
    val template: MutableMap<Char, Long>,
    val pairsInTemplate: MutableMap<PolymerPair, Long>
) {
    constructor(rules: PolymerRules, template: String) : this(rules, template.associate {
        it to template.count { it2 -> it == it2 }.toLong()
    }.toMutableMap(), template.toPolymerTemplate().toMutableMap())

    fun step() {
        val addedPairs = mutableMapOf<PolymerPair, Long>()

        for ((pair, count) in pairsInTemplate) {
            if (!rules.contains(pair) || count == 0.toLong()) {
                continue
            }

            val addedElement = rules[pair]!!
            val leftPair = pair[0].toString() + addedElement
            val rightPair = addedElement + pair[1].toString()

            addedPairs[leftPair] = (addedPairs[leftPair] ?: 0) + count
            addedPairs[rightPair] = (addedPairs[rightPair] ?: 0) + count
            addedPairs[pair] = (addedPairs[pair] ?: 0) - count

            // update frequency counter
            template[addedElement] = (template[addedElement] ?: 0) + count
        }

        for ((pair, change) in addedPairs) {
            pairsInTemplate[pair] = (pairsInTemplate[pair] ?: 0) + change
        }
    }

    val elements: Map<Char, Long>
        get() = template
}

fun List<String>.toPolymerRules(): PolymerRules = this.associate {
    val (polymerPair, insertedElement) = it.split(" -> ")
    polymerPair to insertedElement[0]
}

fun String.toPolymerTemplate(): PolymerTemplate =
    this.windowed(2)
        .fold(mapOf()) { template, it ->
            template + (it to template.getOrDefault(it, 0) + 1)
        }

fun part1(input: List<String>): Long {
    val polymer = Polymer(input.drop(2).toPolymerRules(), input.first())

    repeat(10) {
        polymer.step()
    }

    val elements = polymer.elements
    return elements.maxOf { it.value } - elements.minOf { it.value }
}

fun part2(input: List<String>): Long {
    val polymer = Polymer(input.drop(2).toPolymerRules(), input.first())

    repeat(40) {
        polymer.step()
    }

    val elements = polymer.elements
    return elements.maxOf { it.value } - elements.minOf { it.value }
}

fun main() {
    val sample = readInput(14, "sample")
    val input = readInput(14, "input")

    check(part1(sample) == 1588.toLong())
    println(part1(input))

    check(part2(sample) == 2188189693529)
    println(part2(input))
}
