package acurast.codec.type.marketplace

import acurast.codec.extensions.*
import acurast.codec.type.AssetId
import acurast.codec.type.Fungibility
import acurast.codec.type.JunctionV1
import acurast.codec.type.JunctionsV1
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class Test {
    @Test
    fun decodeJobAssignment() {
        val match = listOf("0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d734a803acc398e9201cfa3e321aa42c21fc213b320e36dae97c125f9f459d32c8d34c356e8294ec3b01cec015cc1b37ca4ee7fbc6e211d0b9d5197bfe177d80c251cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07cd4697066733a2f2f516d5151656a454856664d4e743774574b716b54614b705533697453425235646e425833476e46487242356f3676","0x00000000000000000000010300a10f0432055800a68601000114000000000000000000000000000000")
        val jobAssignment = JobAssignment.read(match)

        Assert.assertEquals(jobAssignment.jobId.script.toHex(), "697066733a2f2f516d5151656a454856664d4e743774574b716b54614b705533697453425235646e425833476e46487242356f3676")
        Assert.assertEquals(jobAssignment.jobId.requester.toU8a().toHex(), "1cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c")
        Assert.assertEquals(jobAssignment.processor.toU8a().toHex(),"c213b320e36dae97c125f9f459d32c8d34c356e8294ec3b01cec015cc1b37ca4")
        Assert.assertEquals(jobAssignment.slot, 0)

        Assert.assertEquals(jobAssignment.feePerExecution.id.kind, AssetId.Kind.Concrete)
        val location = jobAssignment.feePerExecution.id.getConcrete()
        Assert.assertEquals(location.parents, 1)
        Assert.assertEquals(location.interior.kind, JunctionsV1.Kind.X3)
        Assert.assertEquals(location.interior.kind, JunctionsV1.Kind.X3)
        Assert.assertEquals(location.interior.junctions.size, 3)
        Assert.assertEquals(location.interior.junctions[0].kind, JunctionV1.Kind.Parachain)
        Assert.assertEquals(location.interior.junctions[0].getParachain(), 1000)
        Assert.assertEquals(location.interior.junctions[1].kind, JunctionV1.Kind.PalletInstance)
        Assert.assertEquals(location.interior.junctions[1].getPalletInstance(), 50)
        Assert.assertEquals(location.interior.junctions[2].kind, JunctionV1.Kind.GeneralIndex)
        Assert.assertEquals(location.interior.junctions[2].getGeneralIndex(), BigInteger("22"))

        Assert.assertEquals(jobAssignment.feePerExecution.fungibility.kind, Fungibility.Kind.Fungible)
        Assert.assertEquals(jobAssignment.feePerExecution.fungibility.amount, BigInteger.valueOf(25_001))

        Assert.assertEquals(jobAssignment.sla.total, 20)
        Assert.assertEquals(jobAssignment.sla.met, 0)

        Assert.assertEquals(jobAssignment.acknowledged, true)
    }
}
