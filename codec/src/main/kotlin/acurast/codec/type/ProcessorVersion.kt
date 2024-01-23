package acurast.codec.type

import acurast.codec.extensions.toU8a
import java.nio.ByteBuffer

public data class ProcessorVersion(
    public val platform: Platform,
    public val buildNumber: UInt,
) : ToU8a {

    override fun toU8a(): ByteArray =
        platform.value.toU8a() + buildNumber.toU8a()

    public companion object {
        public fun read(value: ByteBuffer): ProcessorVersion {
            val platform =  Platform.read(value)
            val buildNumber = value.int.toUInt()

            return ProcessorVersion(platform, buildNumber)
        }
    }
}