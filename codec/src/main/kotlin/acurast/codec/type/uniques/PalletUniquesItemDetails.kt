package acurast.codec.type.uniques

import acurast.codec.type.MultiAddress
import acurast.codec.type.readAccountId
import java.nio.ByteBuffer

public data class PalletUniquesItemDetails(val owner: MultiAddress) {
    public companion object {
        public fun read(bytes: ByteBuffer): PalletUniquesItemDetails {
            val owner = bytes.readAccountId()

            return PalletUniquesItemDetails(owner)
        }
    }
}