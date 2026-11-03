package acurast.codec.extensions

import org.junit.Assert
import org.junit.Test

class XXHashTest {
    @Test
    fun `xxH128 - matches the Substrate twox128 storage prefixes`() {
        Assert.assertEquals("26aa394eea5630e07c48ae0c9558cef7", "System".toByteArray().xxH128().toHex())
        Assert.assertEquals("b99d880ec681799c0cf30e8886371da9", "Account".toByteArray().xxH128().toHex())
    }

    @Test
    fun `xxH64 - matches the first half of xxH128`() {
        Assert.assertEquals("26aa394eea5630e0", "System".toByteArray().xxH64().toHex())
    }
}
