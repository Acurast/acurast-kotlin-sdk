package acurast.codec.type

import java.nio.ByteBuffer

public data class ProcessorVersion(
    public val platform: Platform,
    public val buildNumber: UInt,
) {
    public companion object {
        public fun read(value: ByteBuffer): ProcessorVersion {
            val platform =  Platform.read(value)
            val buildNumber = value.int.toUInt()

            return ProcessorVersion(platform, buildNumber)
        }
    }
}