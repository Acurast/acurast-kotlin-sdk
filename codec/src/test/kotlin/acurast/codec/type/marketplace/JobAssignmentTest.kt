package acurast.codec.type.marketplace

import acurast.codec.extensions.*
import acurast.codec.type.acurast.MultiOrigin
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class Test {
    @Test
    fun decodeJobAssignment() {
        val match = listOf("0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d73bda717e2c1dbd94fc3adce6ba1ba9c0fd80a8b0d800a3320528693947f7317871b2d51e5f3c8f3d0d4e4f7e6938ed68f8eab5b223dc34c614476ea3d8bb6dd5900d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d03000000000000000000000000000000","0x00d007000000000000ea030000000000000000000000000000011e1c000000000000000000000000000008008402afe7b554c9bf483bff2e893c683cfdb8f1dcf17ef3b2e4d1067d48c8a50467aa018402ee37f55f791cf10d2fa2bb7c9743bd30226fdfe5364465b9903cf3eb159e98f5")
        val jobAssignment = JobAssignment.read(match, 0u)

        Assert.assertEquals(jobAssignment.processor.toU8a().toHex(),"d80a8b0d800a3320528693947f7317871b2d51e5f3c8f3d0d4e4f7e6938ed68f")
        Assert.assertEquals(MultiOrigin.Kind.Acurast, jobAssignment.jobId.origin.kind)
        Assert.assertEquals("d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d", jobAssignment.jobId.origin.source.toHex())
        Assert.assertEquals(BigInteger("3"), jobAssignment.jobId.id)

        Assert.assertEquals(jobAssignment.slot, 0)

        Assert.assertEquals(jobAssignment.feePerExecution.x, BigInteger.valueOf(1002))

        Assert.assertEquals(jobAssignment.sla.total, 7198)
        Assert.assertEquals(jobAssignment.sla.met, 0)

        Assert.assertEquals(jobAssignment.acknowledged, true)
    }

    @Test
    fun decodeJobAssignment2() {
        val match = listOf("0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d73bda717e2c1dbd94fc3adce6ba1ba9c0fd80a8b0d800a3320528693947f7317871b2d51e5f3c8f3d0d4e4f7e6938ed68f8eab5b223dc34c614476ea3d8bb6dd5900d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d03000000000000000000000000000000","0x00d007000000000000ea030000000000000000000000000000011e1c000000000000000000000000000008008402afe7b554c9bf483bff2e893c683cfdb8f1dcf17ef3b2e4d1067d48c8a50467aa018402ee37f55f791cf10d2fa2bb7c9743bd30226fdfe5364465b9903cf3eb159e98f5")
        val jobAssignment = JobAssignment.read(match, 0u)

        Assert.assertEquals(jobAssignment.processor.toU8a().toHex(),"d80a8b0d800a3320528693947f7317871b2d51e5f3c8f3d0d4e4f7e6938ed68f")
        Assert.assertEquals(MultiOrigin.Kind.Acurast, jobAssignment.jobId.origin.kind)
        Assert.assertEquals("d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d", jobAssignment.jobId.origin.source.toHex())
        Assert.assertEquals(BigInteger("3"), jobAssignment.jobId.id)

        Assert.assertEquals(jobAssignment.slot, 0)

        Assert.assertEquals(jobAssignment.feePerExecution.x, BigInteger.valueOf(1002))

        Assert.assertEquals(7198, jobAssignment.sla.total)
        Assert.assertEquals(0, jobAssignment.sla.met)

        Assert.assertEquals(jobAssignment.acknowledged, true)
    }
}
