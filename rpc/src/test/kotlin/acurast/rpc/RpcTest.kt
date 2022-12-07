package acurast.rpc

import acurast.codec.extensions.hexToBa
import acurast.rpc.http.IHttpClientProvider
import acurast.rpc.pallet.JSONRequest
import acurast.rpc.type.FrameSystemAccountInfo
import acurast.rpc.type.PalletAssetsAssetAccount
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
import java.math.BigInteger
import kotlin.test.assertEquals

class RpcTest {
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
    fun `Get Account Information`() {
        val account = "0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"
        val param = JSONArray().put("26aa394eea5630e07c48ae0c9558cef7b99d880ec681799c0cf30e8886371da9de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
        val body = JSONRequest("state_getStorage", param).toString()

        val expectedResponse = FrameSystemAccountInfo(
            nonce = 0U,
            consumers = 1U,
            providers = 1U,
            sufficients = 0U,
            data = FrameSystemAccountInfo.FrameSystemAccountInfoData(
                free = BigInteger("1152921504606846976"),
                reserved = BigInteger.ZERO,
                miscFrozen = BigInteger.ZERO,
                feeFrozen = BigInteger.ZERO,
            )
        )

        val jsonResponse = """                	
            {
                "jsonrpc": "2.0",
                "result": "0x0000000001000000010000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                "id": 1
            }
        """.trimIndent()

        coEvery { httpClient.post(any(), any(), any(), any()) } returns jsonResponse

        val response = runBlocking {
            rpc.getAccountInfo(account.hexToBa())
        }

        assertEquals(expectedResponse, response)

        coVerify { httpClient.post(rpcURL, body = body, ) }
    }

    @Test
    fun `Get Account Asset Information`() {
        val assetId = 10
        val account = "0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"
        val param = JSONArray().put("682a59d51ab9e48a8c8cc418ff9708d2b99d880ec681799c0cf30e8886371da91523c4974e05c5b917b6037dec663b5d0a000000de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
        val body = JSONRequest("state_getStorage", param).toString()

        val expectedResponse = PalletAssetsAssetAccount(
            balance = BigInteger("99999999999999999999999999000000000000"),
            isFrozen = false,
            reason = 0,
        )

        val jsonResponse = """                	
            {
                "jsonrpc": "2.0",
                "result": "0x00f05a2b57218a097ac4865aa84c3b4b0000",
                "id": 1
            }
        """.trimIndent()

        coEvery { httpClient.post(any(), any(), any(), any()) } returns jsonResponse

        val response = runBlocking {
            rpc.getAccountAssetInfo(assetId, account.hexToBa())
        }

        assertEquals(expectedResponse, response)

        coVerify { httpClient.post(rpcURL, body = body, ) }
    }
}