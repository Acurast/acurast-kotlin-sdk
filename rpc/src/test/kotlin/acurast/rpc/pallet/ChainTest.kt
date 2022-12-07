package acurast.rpc.pallet

import acurast.rpc.RPC
import acurast.rpc.http.IHttpClientProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ChainTest {
    @MockK
    private lateinit var httpClient : IHttpClientProvider
    private lateinit var rpc: RPC

    private val rpcURL = "https://example.com"

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        rpc = RPC(rpcURL, httpClient)
    }

    @After
    fun clean() {
        unmockkAll()
    }

    @Test
    fun `Get Block Hash`() {
        val body = JSONRequest("chain_getBlockHash", JSONArray()).toString()

        val expectedResponse = "0x12664e6b6d3eb4dcf6c594a30f5e9e79ab980511a5f7e07ce490e454165caa7d"

        val jsonResponse = """                	
            {
                "jsonrpc": "2.0",
                "result": "0x12664e6b6d3eb4dcf6c594a30f5e9e79ab980511a5f7e07ce490e454165caa7d",
                "id": 1
            }
        """.trimIndent()

        coEvery { httpClient.post(any(), any(), any(), any()) } returns jsonResponse

        val response = runBlocking {
            rpc.chain.getBlockHash()
        }

        assertEquals(expectedResponse, response)

        coVerify { httpClient.post(rpcURL, body = body, ) }
    }

}