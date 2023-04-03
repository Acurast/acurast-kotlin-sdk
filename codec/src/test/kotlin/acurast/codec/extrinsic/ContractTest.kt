package acurast.codec.extrinsic

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.*
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class ContractTest {
    @Test
    fun encodeContractCallExtrinsic() {
        val callIndex = byteArrayOf(0x46, 0x06)
        val destination = MultiAddress(AccountIdentifier.AccountID, "4370e5685ad78b44a6be764cbc4e2a6bd18f7fa6cc2a8d302333e03979db7e9c".hexToBa())
        val value = Compact(UInt128(BigInteger.ZERO))
        val refTime = Compact(UInt64(3951114240))
        val proofSize = Compact(UInt64(629760))
        val storageDepositLimit = Option.some(Compact(UInt128(BigInteger.ZERO)))
        val data = "89c2c64f01000000000000000000000000000000".hexToBa()

        val call = ContractCall(callIndex, destination, value, refTime, proofSize, storageDepositLimit, data)
        val extrinsic = Extrinsic(
            ExtrinsicSignature(
                MultiAddress(AccountIdentifier.AccountID, "6cfec9750b7cc3d2a14a8f0a884142a32553e43eee334a0f59ad6a98f4bbff0b".hexToBa()),
                MultiSignature(CurveKind.Sr25519, "c4e12781fd4e55df86176c155c8c46b648590ff8d6c3af33cc756bc9f8e77b25fac2b069cfb6bdfb9238e3d986f7f743cebba21c5f66c50bc93fc167fe17c48b".hexToBa()),
                MortalEra(32, 19),
                2,
                BigInteger.ZERO
            ),
            call
        )

        Assert.assertEquals(
            "ad0284006cfec9750b7cc3d2a14a8f0a884142a32553e43eee334a0f59ad6a98f4bbff0b01c4e12781fd4e55df86176c155c8c46b648590ff8d6c3af33cc756bc9f8e77b25fac2b069cfb6bdfb9238e3d986f7f743cebba21c5f66c50bc93fc167fe17c48b340108004606004370e5685ad78b44a6be764cbc4e2a6bd18f7fa6cc2a8d302333e03979db7e9c0003003881eb0270260001005089c2c64f01000000000000000000000000000000",
            extrinsic.toU8a().toHex(),
        )
    }
}