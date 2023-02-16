package acurast.codec.extrinsic

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.*
import acurast.codec.type.manager.ProcessorPairing
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class ManagerTest {
    @Test
    fun encodeHeartbeatCall() {
        val callIndex = byteArrayOf(0x29, 0x03);
        val call = HeartbeatCall(callIndex);
        Assert.assertEquals("2903", call.toU8a().toHex())
    }

    @Test
    fun encodePairWithManagerCall() {
        val callIndex = byteArrayOf(0x29, 0x01)
        val pairing = ProcessorPairing(
            account = AccountId32("8eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48".hexToBa()),
            proof = Option.some(
                ProcessorPairing.Proof(
                    timestamp = UInt128(BigInteger("1000000000000000000000")),
                    signature = MultiSignature(CurveKind.Secp256r1, "0x3eddb7396bae2edaccedde8d7a728c2ff6e8c1240fbf7c1415955cee5713c831fa9783f7e9291ac03a556fafe99b4ff0be1612c98ccabb80926f26b1eb683f8401".hexToBa())
                )
            )
        )
        val call = PairWithManagerCall(callIndex, pairing);
        Assert.assertEquals("29018eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48010000a0dec5adc9353600000000000000033eddb7396bae2edaccedde8d7a728c2ff6e8c1240fbf7c1415955cee5713c831fa9783f7e9291ac03a556fafe99b4ff0be1612c98ccabb80926f26b1eb683f8401", call.toU8a().toHex())
    }
}