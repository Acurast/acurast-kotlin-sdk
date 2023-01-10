package acurast.codec.type

import acurast.codec.extensions.toU8a
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Supported account identifiers.
 */
public enum class AccountIdentifier(public val id: Int) {
    AccountID(0),
    AccountIndex(1),
    Raw(2),
    Address32(3),
    Address20(4)
}

/**
 * The signer account identifier used in the scale encoding.
 */
public class MultiAddress(
    public val type: AccountIdentifier,
    public val bytes: ByteArray
): ToU8a {
    public override fun toU8a(): ByteArray {
        return type.id.toByte().toU8a() + bytes
    }
}

/**
 * An account identifier with 32 bytes.
 */
public class AccountId32(private val bytes: ByteArray): ToU8a {
    public override fun toU8a(): ByteArray {
        return bytes
    }
}

public fun ByteBuffer.readAccountId(): MultiAddress {
    this.order(ByteOrder.LITTLE_ENDIAN)
    val ba = ByteArray(32)
    get(ba)
    return MultiAddress(AccountIdentifier.AccountID, ba)
}

public fun ByteBuffer.readAccountId32(): AccountId32 {
    this.order(ByteOrder.LITTLE_ENDIAN)
    val ba = ByteArray(32)
    get(ba)
    return AccountId32(ba)
}