package acurast.rpc.pallet

import acurast.rpc.AcurastRpc
import acurast.rpc.engine.RpcEngine
import acurast.rpc.engine.http.IHttpClientProvider
import acurast.rpc.engine.request
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
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
    private lateinit var engine: RpcEngine
    private lateinit var acurastRpc: AcurastRpc


    @Before
    fun setup() {
        MockKAnnotations.init(this)
        acurastRpc = AcurastRpc(engine)
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

        coEvery { engine.request(any(), any()) } returns jsonResponse

        val response = runBlocking {
            acurastRpc.chain.getBlockHash()
        }

        assertEquals(expectedResponse, response)

        coVerify { engine.request(body = matchJsonRpcRequest(method, params), timeout = any()) }
    }

}