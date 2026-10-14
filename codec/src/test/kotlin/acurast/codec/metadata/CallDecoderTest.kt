package acurast.codec.metadata

import acurast.codec.extensions.ScaleDecoderException
import acurast.codec.extensions.hexToBa
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigInteger
import java.util.zip.GZIPInputStream
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class CallDecoderTest {

    @Test
    fun `decode - reads the pallets of the Canary runtime metadata`() {
        val metadata = canaryMetadata()

        assertEquals("Balances", metadata.pallet(10)?.name)
        assertEquals("AcurastMarketplace", metadata.pallet(43)?.name)
        assertTrue(metadata.pallets.size > 40)
    }

    @Test
    fun `decode - rejects data that is not runtime metadata`() {
        assertFailsWith<ScaleDecoderException> { RuntimeMetadata.decode(byteArrayOf(1, 2, 3, 4, 14)) }
    }

    @Test
    fun `decodeCall - decodes a transfer with its arguments`() {
        val call = decodeCall(TRANSFER.hexToBa(), canaryMetadata())

        assertEquals("Balances", call.pallet)
        assertEquals("transfer_keep_alive", call.name)
        assertEquals("balances.transferKeepAlive", call.id)
        val (dest, value) = call.arguments
        assertEquals("dest", dest.name)
        val address = assertIs<Value.Variant>(dest.value)
        assertEquals("Id", address.name)
        assertIs<Value.Account>(address.fields.single().value)
        assertEquals("value", value.name)
        assertEquals(Value.Number(BigInteger.valueOf(1_234_567_890_000)), value.value)
    }

    @Test
    fun `decodeCall - decodes the calls nested in a batch`() {
        val call = decodeCall(BATCH.hexToBa(), canaryMetadata())

        assertEquals("utility.batchAll", call.id)
        assertEquals(
            listOf("utility.batchAll", "balances.transferKeepAlive", "system.remark"),
            call.allCalls().map { it.id },
        )
        assertEquals(listOf("balances.transferKeepAlive", "system.remark"), call.arguments.single().directCalls().map { it.id })
    }

    @Test
    fun `decodeCall - decodes every call of the runtime exactly`() {
        val metadata = canaryMetadata()
        val calls = canaryCalls().filter { it.first != XCM_PALLET }

        val failures = calls.mapNotNull { (pallet, name, method) ->
            runCatching { decodeCall(method.hexToBa(), metadata) }.fold(
                onSuccess = { call -> "$pallet.$name decoded as ${call.pallet}.${call.name}".takeIf { call.pallet != pallet || call.name != name } },
                onFailure = { "$pallet.$name ($method): ${it.message}" },
            )
        }

        assertEquals(emptyList<String>(), failures)
        assertTrue(calls.size > 250)
    }

    @Test
    fun `decodeCall - rejects variants the runtime does not define`() {
        val send = canaryCalls().single { it.first == XCM_PALLET && it.second == "send" }

        assertFailsWith<ScaleDecoderException> { decodeCall(send.third.hexToBa(), canaryMetadata()) }
    }

    @Test
    fun `decodeCall - rejects trailing call data`() {
        assertFailsWith<ScaleDecoderException> { decodeCall((TRANSFER + "00").hexToBa(), canaryMetadata()) }
    }

    @Test
    fun `decodeCall - rejects truncated call data`() {
        assertFailsWith<Exception> { decodeCall(TRANSFER.dropLast(2).hexToBa(), canaryMetadata()) }
    }

    @Test
    fun `decodeCall - rejects unknown pallets`() {
        assertFailsWith<ScaleDecoderException> { decodeCall("0xfe00".hexToBa(), canaryMetadata()) }
    }

    companion object {
        const val TRANSFER = "0x0a0300b4ce485884bb50a79e159f1c6bb3345869210345f954eb9ea2f5066c3f416a430b5004fb711f01"
        const val BATCH =
            "0x0802080a0300b4ce485884bb50a79e159f1c6bb3345869210345f954eb9ea2f5066c3f416a430b5004fb711f0100001468656c6c6f"

        /**
         * polkadot-js encodes the default of versioned XCM enums with variant index 0, which the
         * runtime does not define (only V3 to V5), so these fixtures are invalid call data.
         */
        private const val XCM_PALLET = "PolkadotXcm"

        private val metadata: RuntimeMetadata by lazy { RuntimeMetadata.decode(resource("canary-metadata.scale.gz")) }

        /** Runtime metadata of Acurast Canary, spec 60, as returned by `state_getMetadata`. */
        fun canaryMetadata(): RuntimeMetadata = metadata

        /** Every call of the Canary runtime, encoded by polkadot-js with default arguments. */
        private fun canaryCalls(): List<Triple<String, String, String>> =
            String(resource("canary-calls.tsv.gz")).lines().filter { it.isNotBlank() }.map {
                val (pallet, call, method) = it.split("\t")
                Triple(pallet, call, method)
            }

        private fun resource(name: String): ByteArray =
            GZIPInputStream(checkNotNull(CallDecoderTest::class.java.classLoader?.getResourceAsStream(name))).use { it.readBytes() }
    }
}
