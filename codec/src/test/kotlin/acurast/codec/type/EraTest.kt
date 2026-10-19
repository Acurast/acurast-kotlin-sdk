package acurast.codec.type

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import org.junit.Test
import org.junit.Assert.*

class Test {
    @Test
    fun encodeEra() {
        assertEquals(
            MortalEra(64,55).toU8a().toHex(),
            "7503"
        )
    }

    @Test
    fun decodeMortalEra() {
        val era = java.nio.ByteBuffer.wrap("c501".hexToBa()).readExtrinsicEra()

        assertEquals(MortalEra(64, 28), era)
        assertEquals("c501", era.toU8a().toHex())
        assertEquals(12_317_020L, (era as MortalEra).birthBlock(12_317_020))
        assertEquals(12_317_084L, era.deathBlock(12_317_020))
    }

    @Test
    fun decodeImmortalEra() {
        assertEquals(ImmortalEra(), java.nio.ByteBuffer.wrap(byteArrayOf(0)).readExtrinsicEra())
    }

    @Test(expected = acurast.codec.extensions.ScaleDecoderException::class)
    fun decodeEraRejectsInvalidPhase() {
        java.nio.ByteBuffer.wrap("5100".hexToBa()).readExtrinsicEra()
    }
}
