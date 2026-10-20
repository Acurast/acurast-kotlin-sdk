package acurast.codec.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class SS58Test {
    private val alice = "d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa()

    @Test
    fun toSS58_encodesWithTheAcurastPrefixByDefault() {
        assertEquals("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY", alice.toSS58())
    }

    @Test
    fun toSS58Address_encodesOneBytePrefixes() {
        assertEquals("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY", alice.toSS58Address(42))
    }

    @Test
    fun toSS58Address_encodesTwoBytePrefixes() {
        assertEquals("cEaNSpz4PxFcZ7nT1VEKrKewH67rfx6MfcM6yKojyyPz7qaqp", alice.toSS58Address(64))
        assertEquals("kGkLEU3e3XXkJp2WK4eNpVmSab5xUNL9QtmLPh8QfCL2EgotW", alice.toSS58Address(137))
        assertEquals("yNa8JpqfFB3q8A29rCwSgxvdU94ufJw2yKKxDgznS5m1PoFvn", alice.toSS58Address(16383))
    }

    @Test(expected = IllegalArgumentException::class)
    fun toSS58Address_rejectsOutOfRangePrefixes() {
        alice.toSS58Address(16384)
    }

    @Test
    fun toBase58_encodesPlainBase58() {
        assertEquals("FHNpKmJrUtusuvKPGomAygQqeiks98bdV6yD61Stb6vg", alice.toBase58())
        assertEquals("115Q", "0000ff".hexToBa().toBase58())
    }
}
