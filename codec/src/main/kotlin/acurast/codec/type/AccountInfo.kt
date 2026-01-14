package acurast.codec.type

import acurast.codec.extensions.readU128
import acurast.codec.extensions.readU32
import java.math.BigInteger
import java.nio.ByteBuffer

public data class AccountInfo(
    val nonce: UInt = 0U,
    val consumers: UInt = 0U,
    val providers: UInt = 0U,
    val sufficients: UInt = 0U,
    val data: Data = Data()
) {
    public data class Data(
        var free: BigInteger = BigInteger.ZERO,
        var reserved: BigInteger = BigInteger.ZERO,
        var frozen: BigInteger = BigInteger.ZERO,
    ) {
        public companion object {
            public fun read(bytes: ByteBuffer): Data {
                val free = bytes.readU128()
                val reserved = bytes.readU128()
                val frozen = bytes.readU128()

                return Data(free, reserved, frozen)
            }
        }
    }

    public companion object {
        public fun read(bytes: ByteBuffer): AccountInfo {
            val nonce = bytes.readU32()
            val consumers = bytes.readU32()
            val providers = bytes.readU32()
            val sufficients = bytes.readU32()
            val data = Data.read(bytes)

            return AccountInfo(nonce, consumers, providers, sufficients, data)
        }
    }
}
