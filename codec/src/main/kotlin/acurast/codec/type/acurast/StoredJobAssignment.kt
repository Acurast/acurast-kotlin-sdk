package acurast.codec.type.acurast

import acurast.codec.extensions.readByteArray
import acurast.codec.type.MultiAddress
import acurast.codec.type.readAccountId
import java.nio.ByteBuffer

/**
 * The structure of a Job Assignment.
 */
public data class StoredJobAssignment(
    public val accountId: MultiAddress,
    public val script: ByteArray
)

public fun ByteBuffer.readStoredJobAssignment(): StoredJobAssignment {
    return StoredJobAssignment(readAccountId(), readByteArray())
}