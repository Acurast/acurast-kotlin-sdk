package acurast.codec.type

import java.nio.ByteBuffer

public enum class Platform(public val value: UInt) {
    Android(0u);

    public companion object {
        public fun read(value: ByteBuffer): Platform =
            Platform.entries.first { it.value == value.int.toUInt() }

    }
}