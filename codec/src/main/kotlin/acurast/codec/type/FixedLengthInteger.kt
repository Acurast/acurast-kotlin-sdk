package acurast.codec.type

import acurast.codec.extensions.toU8a
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder

public data class UInt128(val x: BigInteger): ToU8a {
    override fun toU8a(): ByteArray {
        val ret = ByteBuffer.allocate(16)
        ret.order(ByteOrder.LITTLE_ENDIAN)
        ret.put(x.toByteArray())

        return ret.array()
    }
}

public data class UInt64(val x: Long): ToU8a {
    override fun toU8a(): ByteArray {
        return x.toU8a()
    }
}