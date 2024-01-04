package acurast.codec.type.contracts

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.Option
import acurast.codec.type.UInt128
import acurast.codec.type.Weight
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import java.nio.ByteBuffer

class ContractApiCallTest {
    @Test
    fun decodeContractApiCall() {
        val result = ByteBuffer.wrap("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27db174d2890a1304b272a00cbee26c5c63617da15037920af2988ba0a94d0a7cdf000000000000000000000000000000000000103546cca1".hexToBa())
        val contractResult = ContractApiCall.read(result)

        Assert.assertEquals(contractResult.origin.toHex(), "d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
        Assert.assertEquals(contractResult.destination.toHex(), "b174d2890a1304b272a00cbee26c5c63617da15037920af2988ba0a94d0a7cdf")
        Assert.assertEquals(contractResult.value, UInt128(BigInteger.ZERO))
        Assert.assertEquals(contractResult.gasLimit.toU8a().toHex(), Option.none<Weight>().toU8a().toHex())
        Assert.assertEquals(contractResult.storageDepositLimit.toU8a().toHex(), Option.none<UInt128>().toU8a().toHex())
        Assert.assertEquals(contractResult.data.toHex(), "3546cca1")
    }
}