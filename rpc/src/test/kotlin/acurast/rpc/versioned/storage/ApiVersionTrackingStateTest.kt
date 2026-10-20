package acurast.rpc.versioned.storage

import acurast.codec.extensions.toHex
import acurast.rpc.AcurastProcessorManager_ApiVersion
import acurast.rpc.engine.RpcEngine
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import matcher.matchJsonRpcRequest
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ApiVersionTrackingStateTest {

    private val apiVersionKey = AcurastProcessorManager_ApiVersion().toHex()

    @Test
    fun `getStorage - reads the value and the api version in one request`() {
        val engine = engineReturning(changes = listOf("0x$KEY_A" to "0x2a", "0x$apiVersionKey" to "0x02000000"))
        val reported = mutableListOf<UInt>()

        val value = runBlocking { ApiVersionTrackingState { reported += it }.getStorage(KEY_A_BYTES, engine = engine) }

        assertEquals("0x2a", value)
        assertEquals(listOf(2u), reported)
        coVerify(exactly = 1) { engine.request(any(), any()) }
        coVerify { engine.request(body = matchJsonRpcRequest("state_queryStorageAt", JSONArray().put(JSONArray().put(KEY_A).put(apiVersionKey))), timeout = any()) }
    }

    @Test
    fun `getStorage - returns null for a missing value`() {
        val engine = engineReturning(changes = listOf("0x$KEY_A" to null, "0x$apiVersionKey" to "0x01000000"))

        val value = runBlocking { ApiVersionTrackingState {}.getStorage(KEY_A_BYTES, engine = engine) }

        assertNull(value)
    }

    @Test
    fun `queryStorageAt - returns only the keys asked for, in order`() {
        val engine = engineReturning(changes = listOf("0x$KEY_A" to "0x01", "0x$KEY_B" to "0x02", "0x$apiVersionKey" to "0x01000000"))
        val reported = mutableListOf<UInt>()

        val results = runBlocking { ApiVersionTrackingState { reported += it }.queryStorageAt(listOf(KEY_A, KEY_B), engine = engine) }

        assertEquals(listOf(listOf("0x$KEY_A", "0x01"), listOf("0x$KEY_B", "0x02")), results.single().changes)
        assertEquals(listOf(1u), reported)
    }

    @Test
    fun `queryStorageAt - reports version 0 when the api version is not set`() {
        val engine = engineReturning(changes = listOf("0x$KEY_A" to "0x01", "0x$apiVersionKey" to null))
        val reported = mutableListOf<UInt>()

        runBlocking { ApiVersionTrackingState { reported += it }.queryStorageAt(listOf(KEY_A), engine = engine) }

        assertEquals(listOf(0u), reported)
    }

    @Test
    fun `queryStorageAt - reports nothing when the node returns no api version`() {
        val engine = engineReturning(changes = listOf("0x$KEY_A" to "0x01"))
        val reported = mutableListOf<UInt>()

        runBlocking { ApiVersionTrackingState { reported += it }.queryStorageAt(listOf(KEY_A), engine = engine) }

        assertEquals(emptyList(), reported)
    }

    private fun engineReturning(changes: List<Pair<String, String?>>): RpcEngine {
        val changesJson = JSONArray().apply {
            changes.forEach { (key, value) -> put(JSONArray().put(key).put(value ?: JSONObject.NULL)) }
        }
        val response = JSONObject()
            .put("jsonrpc", "2.0")
            .put("id", 1)
            .put("result", JSONArray().put(JSONObject().put("block", "0x00").put("changes", changesJson)))

        return mockk { coEvery { request(any(), any()) } returns response }
    }

    private companion object {
        const val KEY_A = "aa"
        const val KEY_B = "bb"
        val KEY_A_BYTES = byteArrayOf(0xaa.toByte())
    }
}
