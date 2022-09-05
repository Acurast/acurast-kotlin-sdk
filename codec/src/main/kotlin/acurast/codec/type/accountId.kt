package acurast.codec.type

import acurast.codec.toU8a

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
    private val type: AccountIdentifier,
    private val bytes: ByteArray
): ToU8a {
    public override fun toU8a(): ByteArray {
        return type.id.toByte().toU8a() + bytes
    }
}
