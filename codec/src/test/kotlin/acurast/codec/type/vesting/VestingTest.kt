package acurast.codec.type.vesting

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.readList
import acurast.codec.type.tokenconversion.TokenConversion
import org.junit.Test
import java.math.BigInteger
import java.nio.ByteBuffer
import kotlin.test.assertEquals

class VestingTest {
    @Test
    fun decodeVestingList() {
        val encoded = "0x040080d8d59a52300400000000000000009262cd840400000000000000000000002cbc3900".hexToBa()
        val vesting = ByteBuffer.wrap(encoded).readList { Vesting.read(this) }

        assertEquals(
            listOf(
                Vesting(
                    locked = BigInteger("301832000000000000"),
                    perBlock = BigInteger("19407921810"),
                    startingBlock = 3_783_724U,
                ),
            ),
            vesting,
        )
    }
}