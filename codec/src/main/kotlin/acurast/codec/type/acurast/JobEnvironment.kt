package acurast.codec.type.acurast

import acurast.codec.extensions.calculateHashCode
import acurast.codec.extensions.equals
import acurast.codec.extensions.readByteArray
import acurast.codec.extensions.readList
import java.nio.ByteBuffer

public data class JobEnvironment(public val publicKey: ByteArray, public val vars: List<Variable>) {

    public data class Variable(public val key: String, public val value: ByteArray) {
        public companion object {
            public fun read(bytes: ByteBuffer): Variable {
                val key = bytes.readByteArray().toString(charset = Charsets.UTF_8)
                val value = bytes.readByteArray()

                return Variable(key, value)
            }
        }

        override fun equals(other: Any?): Boolean = equals(other) { key == it.key && value.contentEquals(it.value) }
        override fun hashCode(): Int = listOf(key.hashCode(), value.contentHashCode()).calculateHashCode()
    }

    public companion object {
        public fun read(bytes: ByteBuffer): JobEnvironment {
            val publicKey = bytes.readByteArray()
            val vars = bytes.readList { Variable.read(this) }

            return JobEnvironment(publicKey, vars)
        }
    }

    override fun equals(other: Any?): Boolean = equals(other) { publicKey.contentEquals(it.publicKey) && vars == it.vars }
    override fun hashCode(): Int = listOf(publicKey.contentHashCode(), vars.hashCode()).calculateHashCode()
}