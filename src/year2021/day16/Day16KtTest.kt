package year2021.day16

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Day16KtTest {

    @Test
    fun chunkWithOneElement() {
        val list = listOf(15)
        assertEquals(1u, list.chunk(0, 1))
        assertEquals(3u, list.chunk(0, 2))
        assertEquals(7u, list.chunk(0, 3))
        assertEquals(15u, list.chunk(0, 4))
    }

    @Test
    fun chunk() {
        val list = listOf(15, 12, 10, 3)
        assertEquals(15u.shl(4).or(12u).shl(4).or(10u).shl(4).or(3u), list.chunk(0, 16))
    }

    @Test
    fun chunkWholeInt() {
        val list = listOf(15, 12, 10, 3, 6)
        assertEquals(15u.shl(4).or(12u).shl(4).or(10u).shl(4).or(3u).shl(4).or(6u), list.chunk(0, 20))
    }

    @Test
    fun parsing() {
        assertEquals(
            Packet(
                6.toByte(),
                PacketType.LiteralValue,
                bytes = listOf(7, 14, 5).map(Int::toByte),
            ),
            "D2FE28".toPacket()
        )
    }

    @Test
    fun part1a() {
        assertEquals(16, part1("8A004A801A8002F478"))
    }

    @Test
    fun part1b() {
        assertEquals(12, part1("620080001611562C8802118E34"))
    }

    @Test
    fun part1c() {
        assertEquals(23, part1("C0015000016115A2E0802F182340"))
    }

    @Test
    fun part1d() {
        assertEquals(31, part1("A0016C880162017C3686B18A3D4780"))
    }

    @Test
    fun part2Sum() {
        assertEquals(3, part2("C200B40A82"))
    }

    @Test
    fun part2Product() {
        assertEquals(54, part2("04005AC33890"))
    }

    @Test
    fun part2Minimum() {
        assertEquals(7, part2("880086C3E88112"))
    }

    @Test
    fun part2Maximum() {
        assertEquals(9, part2("CE00C43D881120"))
    }

    @Test
    fun part2LessThan() {
        assertEquals(1, part2("D8005AC2A8F0"))
    }

    @Test
    fun part2GreaterThan() {
        assertEquals(0, part2("F600BC2D8F"))
    }

    @Test
    fun part2EqualTo() {
        assertEquals(0, part2("9C005AC2F8F0"))
    }

    @Test
    fun part2Complex() {
        assertEquals(1, part2("9C0141080250320F1802104A08"))
    }
}