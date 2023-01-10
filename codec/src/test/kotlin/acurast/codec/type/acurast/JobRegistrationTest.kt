package acurast.codec.type.acurast

import acurast.codec.extensions.*
import org.junit.Assert
import org.junit.Test
import java.nio.ByteBuffer

class Test {
    @Test
    fun decodeJobRegistration() {
        val match = ByteBuffer.wrap("0xd4697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f7554320000881300000000000052525aa5850100003a565aa58501000020bf02000000000088130000000000008813000005000000204e00000000000100010300a10f0432055800821a06000000000000000000000000000000000000".hexToBa())
        val jobRegistration = JobRegistration.read(match)

        Assert.assertEquals(jobRegistration.script.toHex(), "697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432")
        Assert.assertEquals(jobRegistration.allowedSources, null)
        Assert.assertEquals(jobRegistration.allowOnlyVerifiedSources, false)

        Assert.assertEquals(jobRegistration.schedule.duration, 5_000)
        Assert.assertEquals(jobRegistration.schedule.startTime, 1_673_516_438_098)
        Assert.assertEquals(jobRegistration.schedule.endTime, 1_673_516_439_098)
        Assert.assertEquals(jobRegistration.schedule.interval, 180_000)
        Assert.assertEquals(jobRegistration.schedule.maxStartDelay, 5_000)

        Assert.assertEquals(jobRegistration.memory, 5_000)
        Assert.assertEquals(jobRegistration.networkRequests, 5)
        Assert.assertEquals(jobRegistration.storage, 20_000)
    }
}
