package acurast.codec.type.marketplace

import acurast.codec.extensions.*
import acurast.codec.type.AssetId
import acurast.codec.type.Fungibility
import acurast.codec.type.JunctionV1
import acurast.codec.type.JunctionsV1
import acurast.codec.type.acurast.MultiOrigin
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class Test {
    @Test
    fun decodeJobAssignment() {
        val match = listOf("0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d733e7c1e81d34082d8f34de585b45ce09353cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f219658239c5938c7ca44e8e83dd55541eb7f001cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c01000000000000000000000000000000","0x00000000000000000000010300a10f0432055800cad401000010000000000000000000000000000000")
        val jobAssignment = JobAssignment.read(match)

        Assert.assertEquals(jobAssignment.processor.toU8a().toHex(),"53cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196")
        Assert.assertEquals(MultiOrigin.Kind.Acurast, jobAssignment.jobId.origin.kind)
        Assert.assertEquals("1cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c", jobAssignment.jobId.origin.source.toHex())
        Assert.assertEquals(BigInteger.ONE, jobAssignment.jobId.id)

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
        Assert.assertEquals(jobAssignment.feePerExecution.fungibility.amount, BigInteger.valueOf(30_002))

        Assert.assertEquals(jobAssignment.sla.total, 16)
        Assert.assertEquals(jobAssignment.sla.met, 0)

        Assert.assertEquals(jobAssignment.acknowledged, false)
    }
}
