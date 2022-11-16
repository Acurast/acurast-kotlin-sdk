package acurast.codec.type

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
}
