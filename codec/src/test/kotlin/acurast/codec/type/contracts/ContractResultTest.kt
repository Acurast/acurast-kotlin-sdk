package acurast.codec.type.contracts

import acurast.codec.extensions.*
import acurast.codec.type.UInt128
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import java.nio.ByteBuffer

class Test {
    @Test
    fun decodeContractResult() {
        val result = ByteBuffer.wrap("0x03bb3d48721ee5020003bb3d4872eae2030001000000000000000000000000000000000000000000004400020000000000000000000000000000000108000000000000004248ed43551702000002120601d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27db174d2890a1304b272a00cbee26c5c63617da15037920af2988ba0a94d0a7cdf08f28e41e4d60e624b9b594f8e0f9cc8752676f1ee060cc3b9fefe45022f8faf07c06326108f5a6a8cb08452e96cfada45862d5420bc8b63dd5221825f6c5fdd2d".hexToBa())
        val contractResult = ContractResult.read(result)

        Assert.assertEquals(contractResult.gas_consumed.refTime, 1917337019)
        Assert.assertEquals(contractResult.gas_consumed.proofSize, 47431)
        Assert.assertEquals(contractResult.gas_required.refTime, 1917337019)
        Assert.assertEquals(contractResult.gas_required.proofSize, 63674)
        Assert.assertEquals(contractResult.storage_deposit, StorageDeposit.Charge(UInt128(BigInteger.ZERO)))
        Assert.assertEquals(contractResult.debug_message.toHex(), "")
        Assert.assertEquals(contractResult.result.toU8a().toHex(), acurast.codec.type.Result.ok<ExecReturnValue, Any>(ExecReturnValue(0u, "0002000000000000000000000000000000".hexToBa())).toU8a().toHex())
    }
}