package acurast.codec.type.manager

import acurast.codec.extensions.readByteArray
import java.nio.ByteBuffer

public data class ProcessorUpdateInfo(
    public val version: ProcessorVersion,
    public val binaryLocation: ByteArray
) {
    public companion object {
        public fun read(value: ByteBuffer): ProcessorUpdateInfo {
            val version = ProcessorVersion.read(value)
            val binaryLocation = value.readByteArray()

            return ProcessorUpdateInfo(version, binaryLocation)
        }
    }
}