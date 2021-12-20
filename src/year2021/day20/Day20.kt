package year2021.day20

import readInput

data class Point(val x: Int, val y: Int)

fun getSquare(pixel: Point): Iterable<Point> =
    (pixel.y - 1..pixel.y + 1).flatMap { y -> (pixel.x - 1..pixel.x + 1).map { x -> Point(x, y) } }

data class TrenchMap(
    val algorithm: List<Char>,
    val map: Map<Point, Char>,
    val default: Char = '.'
) {
    fun get(key: Point): Char = map.getOrDefault(key, default)
    fun get(x: Int, y: Int): Char = get(Point(x, y))

    private val xs: IntRange
        get() = (map.keys.minOf { it.x } - 2..map.keys.maxOf { it.x } + 2)

    private val ys: IntRange
        get() = (map.keys.minOf { it.y } - 2..map.keys.maxOf { it.y } + 2)

    private val indices: Iterable<Point>
        get() = ys.flatMap { y -> xs.map { x -> Point(x, y) } }

    fun print() {
        for (y in ys) {
            for (x in xs) {
                print(get(x, y))
            }
            println()
        }
    }

    private fun toIndex(square: Iterable<Point>): Int =
        square.map { if (get(it) == '#') 1 else 0 }.fold(0) { acc, bit -> acc * 2 + bit }

    private fun enhancePixel(pixel: Point): Char = algorithm[toIndex(getSquare(pixel))]

    fun enhance(): TrenchMap =
        TrenchMap(
            algorithm,
            indices.associateWith { enhancePixel(it) },
            when (default) {
                '.' -> algorithm.first()
                '#' -> algorithm.last()
                else -> error("Invalid char found")
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

fun part1(input: TrenchMap): Int =
    (1..2).fold(input) { acc, i -> acc.enhance() }.map.values.count { it == '#' }

fun part2(input: TrenchMap): Int =
    (1..50).fold(input) { acc, i -> acc.enhance() }.map.values.count { it == '#' }

fun main() {
    val sample = readInput(20, "sample").toTrenchMap()
    val input = readInput(20, "input").toTrenchMap()

    check(part1(sample) == 35)
    println(part1(input))

    check(part2(sample) == 3351)
    println(part2(input))
}
