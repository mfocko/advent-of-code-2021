package year2021.day18

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SnailfishNumberTest {
    @Test
    fun toSnailfishNumber() {
        assertEquals(asSFNumber(1, 2), "[1,2]".toSnailfishNumber())
        assertEquals(
            asSFNumber(asSFNumber(1, 2), 3), "[[1,2],3]".toSnailfishNumber()
        )
        assertEquals(asSFNumber(9, asSFNumber(8, 7)), "[9,[8,7]]".toSnailfishNumber())
        assertEquals(asSFNumber(asSFNumber(1, 9), asSFNumber(8, 5)), "[[1,9],[8,5]]".toSnailfishNumber())
        assertEquals(
            asSFNumber(
                asSFNumber(
                    asSFNumber(asSFNumber(1, 2), asSFNumber(3, 4)), asSFNumber(asSFNumber(5, 6), asSFNumber(7, 8))
                ), 9
            ), "[[[[1,2],[3,4]],[[5,6],[7,8]]],9]".toSnailfishNumber()
        )
        assertEquals(
            asSFNumber(
                asSFNumber(
                    asSFNumber(9, asSFNumber(3, 8)), asSFNumber(asSFNumber(0, 9), 6)
                ), asSFNumber(
                    asSFNumber(asSFNumber(3, 7), asSFNumber(4, 9)), 3
                )
            ), "[[[9,[3,8]],[[0,9],6]],[[[3,7],[4,9]],3]]".toSnailfishNumber()
        )
        assertEquals(
            asSFNumber(
                asSFNumber(
                    asSFNumber(asSFNumber(1, 3), asSFNumber(5, 3)), asSFNumber(asSFNumber(1, 3), asSFNumber(8, 7))
                ), asSFNumber(
                    asSFNumber(asSFNumber(4, 9), asSFNumber(6, 9)), asSFNumber(asSFNumber(8, 2), asSFNumber(7, 3))
                )
            ), "[[[[1,3],[5,3]],[[1,3],[8,7]]],[[[4,9],[6,9]],[[8,2],[7,3]]]]".toSnailfishNumber()
        )
    }

    @Test
    fun reduce() {
        assertEquals(
            "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]".toSnailfishNumber(),
            reduce("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]".toSnailfishNumber())
        )

        assertEquals(
            "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]".toSnailfishNumber(),
            reduce("[[[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]],[[[[4,2],2],6],[8,7]]]".toSnailfishNumber())
        )
    }

    @Test
    fun explode() {
        assertEquals(
            "[[[[0,9],2],3],4]".toSnailfishNumber(),
            "[[[[[9,8],1],2],3],4]".toSnailfishNumber().explode(mutableListOf()).second
        )
        assertEquals(
            "[7,[6,[5,[7,0]]]]".toSnailfishNumber(),
            "[7,[6,[5,[4,[3,2]]]]]".toSnailfishNumber().explode(mutableListOf()).second
        )
        assertEquals(
            "[[6,[5,[7,0]]],3]".toSnailfishNumber(),
            "[[6,[5,[4,[3,2]]]],1]".toSnailfishNumber().explode(mutableListOf()).second
        )
        assertEquals(
            "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]".toSnailfishNumber(),
            "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]".toSnailfishNumber().explode(mutableListOf()).second
        )
        assertEquals(
            "[[3,[2,[8,0]]],[9,[5,[7,0]]]]".toSnailfishNumber(),
            "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]".toSnailfishNumber().explode(mutableListOf()).second
        )
    }

    @Test
    fun split() {
        assertEquals(asSFNumber(5, 5), asSFNumber(10).split(mutableListOf()).second)
        assertEquals(asSFNumber(5, 6), asSFNumber(11).split(mutableListOf()).second)
        assertEquals(asSFNumber(6, 6), asSFNumber(12).split(mutableListOf()).second)
    }

    @Test
    fun getType() {
        assertEquals(SnailfishNumberType.Regular, "13".toSnailfishNumber().type)
        assertEquals(SnailfishNumberType.Pair, "[1,2]".toSnailfishNumber().type)
    }

    @Test
    fun getMagnitude() {
        assertEquals(143, "[[1,2],[[3,4],5]]".toSnailfishNumber().magnitude)
        assertEquals(1384, "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]".toSnailfishNumber().magnitude)
        assertEquals(445, "[[[[1,1],[2,2]],[3,3]],[4,4]]".toSnailfishNumber().magnitude)
        assertEquals(791, "[[[[3,0],[5,3]],[4,4]],[5,5]]".toSnailfishNumber().magnitude)
        assertEquals(1137, "[[[[5,0],[7,4]],[5,5]],[6,6]]".toSnailfishNumber().magnitude)
        assertEquals(3488, "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]".toSnailfishNumber().magnitude)
    }

    @Test
    fun plus() {
        assertEquals(
            "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]".toSnailfishNumber(),
            "[[[[4,3],4],4],[7,[[8,4],9]]]".toSnailfishNumber() + "[1,1]".toSnailfishNumber()
        )
        assertEquals(
            "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]".toSnailfishNumber(),
            "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]".toSnailfishNumber() + "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]".toSnailfishNumber()
        )
    }

    @Test
    fun plusFold() {
        assertEquals(
            "[[[[1,1],[2,2]],[3,3]],[4,4]]".toSnailfishNumber(), listOf(
                "[1,1]", "[2,2]", "[3,3]", "[4,4]"
            ).map(String::toSnailfishNumber).reduce(SpecialNumber::plus)
        )

        assertEquals(
            "[[[[3,0],[5,3]],[4,4]],[5,5]]".toSnailfishNumber(), listOf(
                "[1,1]", "[2,2]", "[3,3]", "[4,4]", "[5,5]",
            ).map(String::toSnailfishNumber).reduce(SpecialNumber::plus)
        )

        assertEquals(
            "[[[[5,0],[7,4]],[5,5]],[6,6]]".toSnailfishNumber(), listOf(
                "[1,1]", "[2,2]", "[3,3]", "[4,4]", "[5,5]", "[6,6]",
            ).map(String::toSnailfishNumber).reduce(SpecialNumber::plus)
        )
    }

    @Test
    fun plusFoldLarger() {
        val numbers = listOf(
            "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]",
            "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]",
            "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]",
            "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]",
            "[7,[5,[[3,8],[1,4]]]]",
            "[[2,[2,2]],[8,[8,1]]]",
            "[2,9]",
            "[1,[[[9,3],9],[[9,0],[0,7]]]]",
            "[[[5,[7,4]],7],1]",
            "[[[[4,2],2],6],[8,7]]",
        ).map(String::toSnailfishNumber)

        val expected = listOf(
            "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]",
            "[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]",
            "[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]",
            "[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]",
            "[[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]",
            "[[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]",
            "[[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]",
            "[[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]",
            "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"
        ).map(String::toSnailfishNumber)

        assertEquals(
            expected.last(), numbers.reduce(SpecialNumber::plus)
        )
    }

    @Test
    fun plusFoldSample() {
        val numbers = listOf(
            "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
            "[[[5,[2,8]],4],[5,[[9,9],0]]]",
            "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
            "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
            "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
            "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
            "[[[[5,4],[7,7]],8],[[8,3],8]]",
            "[[9,3],[[9,9],[6,[4,9]]]]",
            "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
            "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]"
        ).map(String::toSnailfishNumber)

        assertEquals(
            "[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]".toSnailfishNumber(),
            numbers.reduce(SpecialNumber::plus)
        )
    }

    @Test
    fun deepExplode() {
        println(reduce("[[[[9,[3,[2,[3,0]]]],[[7,9],5]],8],9]".toSnailfishNumber()))
    }
}