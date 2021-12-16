package year2021.day16

import readInput

enum class PacketType(val id: Byte) {
    Sum(0),
    Product(1),
    Minimum(2),
    LiteralValue(4),
    Maximum(3),
    GreaterThan(5),
    LessThan(6),
    EqualTo(7),
}

enum class LengthType(val id: Byte) {
    TotalLength(0),
    Count(1)
}

private fun Byte.toPacketType(): PacketType = PacketType.values().first { it.id == this }
private fun Byte.toLengthType(): LengthType = LengthType.values().first { it.id == this }

data class Packet(
    val version: Byte,
    val type: PacketType,
    val bytes: List<Byte> = emptyList(),
    val packets: List<Packet> = emptyList()
) {
    fun versionSum(): Int = when (type) {
        PacketType.LiteralValue -> version.toInt()
        else -> version.toInt() + packets.sumOf(Packet::versionSum)
    }

    fun eval(): Long = when (type) {
        PacketType.Sum -> packets.sumOf(Packet::eval)
        PacketType.Product -> packets.fold(1) { result, packet -> result * packet.eval() }
        PacketType.Minimum -> packets.minOf(Packet::eval)
        PacketType.LiteralValue -> bytes.fold(0) { result, byte -> result.shl(4).or(byte.toLong()) }
        PacketType.Maximum -> packets.maxOf(Packet::eval)
        PacketType.GreaterThan -> if (packets[0].eval() > packets[1].eval()) 1 else 0
        PacketType.LessThan -> if (packets[0].eval() < packets[1].eval()) 1 else 0
        PacketType.EqualTo -> if (packets[0].eval() == packets[1].eval()) 1 else 0
    }
}

fun List<Int>.chunk(from: Int, to: Int): UInt {
    check(to - from <= 32)

    var result = 0u
    var usedBits = 0
    var i = from.div(4)

    // prefix
    if (from.mod(4) != 0) {
        val offset = 4 - from.mod(4)
        val offsetMask = 1.shl(offset) - 1

        result = this[i].and(offsetMask).toUInt()

        usedBits += offset
        i++
    }

    while (4 * i < to) {
        val fromFirst = this[i].toUInt()
        result = result.shl(4).or(fromFirst)

        i++
        usedBits += 4
    }

    return result.shr(usedBits - (to - from))
}

fun parseGroups(bytes: List<Int>, groups: MutableList<Byte>, from: Int): Int {
    val mask = (1.shl(4) - 1).toUInt()

    var i = 0
    var lastByte = false
    while (!lastByte) {
        val byte = bytes.chunk(from + 5 * i, from + 5 * (i + 1))
        groups.add(byte.and(mask).toByte())

        i++
        lastByte = byte.shr(4) == 0u
    }

    return from + 5 * i
}

fun parsePackets(bytes: List<Int>, packets: MutableList<Packet>, from: Int): Int {
    var index = from + 1
    when (bytes.chunk(from, from + 1).toByte().toLengthType()) {
        LengthType.TotalLength -> {
            val packetsSize = bytes.chunk(index, index + 15).toInt()
            index += 15

            var read = 0
            while (read < packetsSize) {
                val (packet, newIndex) = parsePacket(bytes, index)

                packets.add(packet)
                read += newIndex - index
                index = newIndex
            }
        }
        LengthType.Count -> {
            val packetCount = bytes.chunk(index, index + 11).toInt()
            index += 11

            repeat(packetCount) {
                val (packet, newIndex) = parsePacket(bytes, index)

                packets.add(packet)
                index = newIndex
            }
        }
    }

    return index
}

fun parsePacket(bytes: List<Int>, from: Int): Pair<Packet, Int> {
    val version = bytes.chunk(from, from + 3).toByte()
    val typeId = bytes.chunk(from + 3, from + 6).toByte().toPacketType()

    val byteGroups = mutableListOf<Byte>()
    val packets = mutableListOf<Packet>()

    val offset = when (typeId) {
        PacketType.LiteralValue -> parseGroups(bytes, byteGroups, from + 6)
        else -> parsePackets(bytes, packets, from + 6)
    }

    return Pair(Packet(version, typeId, byteGroups, packets), offset)
}

fun String.toPacket(): Packet =
    parsePacket(this.map { it.digitToInt(16) }, 0).first

fun part1(input: String): Int = input.toPacket().versionSum()
fun part2(input: String): Long = input.toPacket().eval()

fun main() {
    val input = readInput(16, "input").first()

    println(part1(input))
    println(part2(input))
}
