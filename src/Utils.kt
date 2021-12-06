import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

private fun openFile(day: Int, name: String) = File("src/day%02d".format(day), "$name.txt")

/**
 * Reads lines from the given input txt file.
 */
fun readInput(day: Int, name: String) = openFile(day, name).readLines()

fun readInputAsInts(day: Int, name: String) = readInput(day, name).map { it.toInt() }
fun readInputAsCommaSeparatedInts(day: Int, name: String) = openFile(day, name)
    .readText()
    .split(",")
    .map { it.toInt() }

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
