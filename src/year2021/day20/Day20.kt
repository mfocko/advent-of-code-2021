package year2021.day20

import product
import readInput

data class Point(val x: Int, val y: Int) {
    val square: Sequence<Point>
        get() = product(y - 1..y + 1, x - 1..x + 1).map { (y, x) -> Point(x, y) }
}

data class TrenchMap(
    val algorithm: List<Char>,
    val map: Map<Point, Char>,
    val default: Char = '.'
) {
    fun get(key: Point): Char = map.getOrDefault(key, default)
    fun get(x: Int, y: Int): Char = get(Point(x, y))

    private fun coordinateRange(getter: (Point) -> Int): IntRange =
        (map.keys.minOf(getter) - 1..map.keys.maxOf(getter) + 1)

    private val xs: IntRange
        get() = coordinateRange { it.x }

    private val ys: IntRange
        get() = coordinateRange { it.y }

    private val indices: Sequence<Point>
        get() = product(ys, xs).map { (y, x) -> Point(x, y) }

    override fun toString(): String =
        ys.joinToString("\n") { y ->
            xs.joinToString("") { x -> get(x, y).toString() }
        }

    private fun toIndex(square: Sequence<Point>): Int =
        square.map { if (get(it) == '#') 1 else 0 }.reduce { acc, bit -> acc * 2 + bit }

    private fun enhancePixel(pixel: Point): Char = algorithm[toIndex(pixel.square)]

    fun enhance(): TrenchMap =
        TrenchMap(
            algorithm,
            indices.associateWith { enhancePixel(it) },
            when (default) {
                '.' -> algorithm.first()
                '#' -> algorithm.last()
                else -> error("Invalid default char found")
            }
        )
}

fun List<String>.toTrenchMap(): TrenchMap =
    TrenchMap(
        this.first().toList(),
        this.drop(2)
            .flatMapIndexed { y, row ->
                row.mapIndexed { x, cell -> Point(x, y) to cell }
            }.toMap()
    )

fun enhance(input: TrenchMap, steps: Int): Int =
    (1..steps).fold(input) { image, _ -> image.enhance() }.map.values.count { it == '#' }

fun part1(input: TrenchMap): Int = enhance(input, 2)
fun part2(input: TrenchMap): Int = enhance(input, 50)

fun main() {
    val sample = readInput(20, "sample").toTrenchMap()
    val input = readInput(20, "input").toTrenchMap()

    check(part1(sample) == 35)
    println(part1(input))

    check(part2(sample) == 3351)
    println(part2(input))
}
