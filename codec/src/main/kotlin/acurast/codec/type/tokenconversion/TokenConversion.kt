package acurast.codec.type.tokenconversion

import acurast.codec.extensions.readCompactInteger
import acurast.codec.extensions.readU128
import acurast.codec.extensions.readU32
import java.math.BigInteger
import java.nio.ByteBuffer

public data class TokenConversion(
    public val amount: BigInteger,
    public val lockStart: UInt
) {
    public companion object {
        public fun read(bytes: ByteBuffer): TokenConversion {
            val amount = bytes.readU128()
            val lockStart = bytes.readU32()

            return TokenConversion(amount, lockStart)
        }
    }
}
