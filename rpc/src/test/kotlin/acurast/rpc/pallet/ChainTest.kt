package acurast.rpc.pallet

import acurast.rpc.AcurastRpc
import acurast.rpc.engine.RpcEngine
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import matcher.matchJsonRpcRequest
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ChainTest {
    @MockK
    private lateinit var rpcEngine: RpcEngine

    private lateinit var chain: Chain


    @Before
    fun setup() {
        MockKAnnotations.init(this)
        chain = Chain()
    }

    @After
    fun clean() {
        unmockkAll()
    }

    @Test
    fun `Get Block Hash`() {
        val method = "chain_getBlockHash"
        val params = JSONArray()

        val expectedResponse = "0x12664e6b6d3eb4dcf6c594a30f5e9e79ab980511a5f7e07ce490e454165caa7d"
        val jsonResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": "0x12664e6b6d3eb4dcf6c594a30f5e9e79ab980511a5f7e07ce490e454165caa7d",
                "id": 1
            }
        """.trimIndent())

        coEvery { rpcEngine.request(any(), any()) } returns jsonResponse

        val response = runBlocking {
            chain.getBlockHash(engine = rpcEngine)
        }

        assertEquals(expectedResponse, response)

        coVerify { rpcEngine.request(body = matchJsonRpcRequest(method, params), timeout = any()) }
    }

}