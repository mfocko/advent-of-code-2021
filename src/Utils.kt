import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

var year: Int = 2021

private fun openFile(day: Int, name: String) = File("src/year%4d/day%02d".format(year, day), "$name.txt")

/**
 * Reads lines from the given input txt file.
 */
fun readInput(day: Int, name: String) = openFile(day, name).readLines()

fun readInputAsInts(day: Int, name: String) = readInput(day, name).map { it.toInt() }
fun readInputAsCommaSeparatedInts(day: Int, name: String) = openFile(day, name)
    .readText()
    .split(",")
    .map { it.toInt() }

fun readGraph(day: Int, name: String) = readInput(day, name).fold(mapOf<String, Set<String>>()) { currentGraph, edge ->
    val (fromVertex, toVertex) = edge.split("-")
    val fromNeighbours = currentGraph.getOrDefault(fromVertex, emptySet()) + toVertex
    val toNeighbours = currentGraph.getOrDefault(toVertex, emptySet()) + fromVertex

    currentGraph + mapOf(fromVertex to fromNeighbours, toVertex to toNeighbours)
}.toMap()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
