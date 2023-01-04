package acurast.codec.type.acurast

import acurast.codec.extensions.*
import acurast.codec.type.AccountIdentifier
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class Test {
    @Test
    fun decodePalletAcurastMarketplaceAssignment() {
        val match = listOf("0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d7323a05cabf6d3bde7ca3ef0d11596b5611cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c01b46a2d8f13769f25fd01fb196526e11cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07cd4697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432","0x0000010300a10f043205580062e4010001")
        val jobAssignment = PalletAcurastMarketplaceAssignment.read(match)

        Assert.assertEquals(jobAssignment.script.toHex(), "697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432")
        Assert.assertEquals(jobAssignment.processor.type, AccountIdentifier.AccountID)
        Assert.assertEquals(jobAssignment.processor.bytes.toHex(), "1cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c")
        Assert.assertEquals(jobAssignment.requester.type, AccountIdentifier.AccountID)
        Assert.assertEquals(jobAssignment.requester.bytes.toHex(), "1cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c")
        Assert.assertEquals(jobAssignment.slot, 0)

        Assert.assertEquals(jobAssignment.feePerExecution.id.kind, AssetIdKind.Concrete)
        val concrete = jobAssignment.feePerExecution.id.getConcrete()
        Assert.assertEquals(concrete.location.parents, 1)
        Assert.assertEquals(concrete.location.interior.kind, JunctionsV1.Kind.X3)
        Assert.assertEquals(concrete.location.interior.kind, JunctionsV1.Kind.X3)
        Assert.assertEquals(concrete.location.interior.junctions.size, 3)
        Assert.assertEquals(concrete.location.interior.junctions[0].kind, JunctionV1.Kind.Parachain)
        Assert.assertEquals(concrete.location.interior.junctions[0].getParachain(), 1000)
        Assert.assertEquals(concrete.location.interior.junctions[1].kind, JunctionV1.Kind.PalletInstance)
        Assert.assertEquals(concrete.location.interior.junctions[1].getPalletInstance(), 50)
        Assert.assertEquals(concrete.location.interior.junctions[2].kind, JunctionV1.Kind.GeneralIndex)
        Assert.assertEquals(concrete.location.interior.junctions[2].getGeneralIndex(), BigInteger("22"))

        Assert.assertEquals(jobAssignment.feePerExecution.fungibility.kind, Fungibility.Kind.Fungible)
        Assert.assertEquals(jobAssignment.feePerExecution.fungibility.amount, BigInteger.valueOf(31_000))

        Assert.assertEquals(jobAssignment.acknowledged, true)
    }
}
