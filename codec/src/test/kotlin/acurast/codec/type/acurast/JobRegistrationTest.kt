package acurast.codec.type.acurast

import acurast.codec.extensions.*
import acurast.codec.type.AccountId32
import org.junit.Assert
import org.junit.Test
import java.nio.ByteBuffer

class Test {
    @Test
    fun decodeJobRegistration() {
        val match = ByteBuffer.wrap("0xd4697066733a2f2f516d6531696468536b35506b6655364a5a776f42537a336e774b367059663169355a356e39574e7968683347737401046fa93ce3f69a3a3517809cb2e34a92fa9fa49f7a767072d6a22fea1b4657f69f00307500000000000064c39f2e87010000a4f7bc338701000060ea0000000000000000000000000000010000000100000001000000000100010300a10f0432055800419c000000000000000000000000000000000000".hexToBa())
        val jobRegistration = JobRegistration.read(match, 0u)

        Assert.assertEquals(jobRegistration.script.toHex(), "697066733a2f2f516d6531696468536b35506b6655364a5a776f42537a336e774b367059663169355a356e39574e79686833477374")
        Assert.assertEquals(jobRegistration.allowedSources?.toU8a()?.toHex(), listOf(AccountId32("6fa93ce3f69a3a3517809cb2e34a92fa9fa49f7a767072d6a22fea1b4657f69f".hexToBa())).toU8a().toHex())
        Assert.assertEquals(jobRegistration.allowOnlyVerifiedSources, false)

        Assert.assertEquals(jobRegistration.schedule.duration, 30_000)
        Assert.assertEquals(jobRegistration.schedule.startTime, 1_680_114_434_916)
        Assert.assertEquals(jobRegistration.schedule.endTime, 1_680_200_234_916)
        Assert.assertEquals(jobRegistration.schedule.interval, 60_000)
        Assert.assertEquals(jobRegistration.schedule.maxStartDelay, 0)

        Assert.assertEquals(jobRegistration.memory, 1)
        Assert.assertEquals(jobRegistration.networkRequests, 1)
        Assert.assertEquals(jobRegistration.storage, 1)
        Assert.assertEquals(jobRegistration.requiredModules.size, 0)
    }
}
