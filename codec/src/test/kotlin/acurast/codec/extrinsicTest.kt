package acurast.codec

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.extrinsic.FulfillCall
import acurast.codec.type.*
import org.junit.Test

import org.junit.Assert.*
import java.math.BigInteger

class Test {
    @Test
    fun encodeEra() {
        assertEquals(
            MortalEra(64,55).toU8a().toHex(),
            "7503"
        )
    }

    @Test
    fun encodeExtrinsic() {
        val script = byteArrayOf(0,18);
        val payload = byteArrayOf(0,18);
        val requester = "d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa();

        val call = FulfillCall(script, payload, requester);
        val extrinsic = Extrinsic(
            ExtrinsicSignature(
                MultiAddress(AccountIdentifier.AccountID, requester),
                MultiSignature(CurveKind.Secp256r1, "6cd7e0ccbf780bc3046fa1af28ee8c68bb992270b6cceeefbc1fc08e492b1807d95400f77ee4259cadd39c3e309119750c4d836d955cc7dc74e8591f8ab78785".hexToBa()),
                MortalEra(64, 55),
                0,
                BigInteger.ZERO,
            ),
            call
        )

        assertEquals(
            extrinsic.toU8a().toHex(),
            "41028400d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d036cd7e0ccbf780bc3046fa1af28ee8c68bb992270b6cceeefbc1fc08e492b1807d95400f77ee4259cadd39c3e309119750c4d836d955cc7dc74e8591f8ab7878575030000280308001208001200d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"
        )
    }
}
