package acurast.codec.type

import acurast.codec.extensions.toCompactU8a
import acurast.codec.extensions.toU8a
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder

public data class UInt128(val x: BigInteger): ToU8a, ToCompactU8a {
    override fun toU8a(): ByteArray {
        val ret = ByteBuffer.allocate(16)
        ret.order(ByteOrder.LITTLE_ENDIAN)
        ret.put(x.toByteArray().reversedArray())

        return ret.array()
    }

    override fun toCompactU8a(): ByteArray {
        return x.toCompactU8a()
    }
}

public data class UInt64(val x: Long): ToU8a, ToCompactU8a {
    override fun toU8a(): ByteArray {
        return x.toU8a()
    }

    override fun toCompactU8a(): ByteArray {
        return x.toCompactU8a()
    }
}

public data class UInt32(val x: Int): ToU8a, ToCompactU8a {
    override fun toU8a(): ByteArray {
        return x.toU8a()
    }

    override fun toCompactU8a(): ByteArray {
        return x.toLong().toCompactU8a()
    }
}

public data class UInt8(val x: Byte): ToU8a, ToCompactU8a {
    override fun toU8a(): ByteArray {
        return x.toU8a()
    }

    override fun toCompactU8a(): ByteArray {
        return x.toCompactU8a()
    }
}

public data class UInt16(val x: Short): ToU8a, ToCompactU8a {
    override fun toU8a(): ByteArray {
        return x.toU8a()
    }

    override fun toCompactU8a(): ByteArray {
        return x.toLong().toCompactU8a()
    }
}