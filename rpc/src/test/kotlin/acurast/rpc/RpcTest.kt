package acurast.rpc

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.AccountId32
import acurast.codec.type.acurast.JobIdentifier
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

    @Test
    fun `Get Job Matches`() {
        val paramGetKeys = "1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d7323a05cabf6d3bde7ca3ef0d11596b5611cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c"
        val bodyGetKeys = JSONRequest("state_getKeys", JSONArray().put(paramGetKeys)).toString()

        val getKeysResponse = """                	
            {
                "jsonrpc": "2.0",
                "result": [
                    "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d7323a05cabf6d3bde7ca3ef0d11596b5611cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c01b46a2d8f13769f25fd01fb196526e11cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07cd4697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432"
                ],
                "id": 1
            }
        """.trimIndent()

        coEvery { httpClient.post(any(), bodyGetKeys, any(), any(), any(), any()) } returns getKeysResponse

        val account = "1cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c"
        val param = JSONArray().put("0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d7323a05cabf6d3bde7ca3ef0d11596b5611cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c01b46a2d8f13769f25fd01fb196526e11cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07cd4697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432")
        val body = JSONRequest("state_queryStorageAt", JSONArray().put(param)).toString()

        val jsonResponse = """                	
            {
                "jsonrpc": "2.0",
                "result": [
                    {
                        "block": "0x40a35a0027b2dae57f8543212c55bae2cbf43a389fac70b304db73dc6f497b87",
                        "changes": [
                            [
                                "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d7323a05cabf6d3bde7ca3ef0d11596b5611cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c01b46a2d8f13769f25fd01fb196526e11cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07cd4697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432",
                                "0x0000010300a10f0432055800a68601000100000000000000000000000000000000"
                            ],
                            [
                                "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d7323a05cabf6d3bde7ca3ef0d11596b5611cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c5bf26ec45ab90e8f1b701097ccad556dd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27dd4697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432",
                                "0x0000010300a10f0432055800a68601000100000000000000000000000000000000"
                            ]
                        ]
                    }
                ],
                "id": 1
            }
        """.trimIndent()

       coEvery { httpClient.post(rpcURL, body, any(), any()) } returns jsonResponse

        val response = runBlocking {
            rpc.getAssignedJobs(account.hexToBa())
        }

        assertEquals(2, response.size)
    }

    @Test
    fun `Get Job Registration`() {
        val account = "1cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c"
        val script = "697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432"

        val param = JSONArray().put("d8f45172ad1e7575680eaa8157102800f86e394d601279aa535d660453ff8e0923a05cabf6d3bde7ca3ef0d11596b5611cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c0d96d7be55af90364f35d6fd733387fbd4697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f755432")
        val body = JSONRequest("state_getStorage", param).toString()

        System.out.println(body.toString())
        val jsonResponse = """                	
            {
                "jsonrpc": "2.0",
                "result": "0xd4697066733a2f2f516d644a4e764d4c66766a7a4a6e48514a6d73454243384b554431667954757346726b5841463559615a6f7554320000881300000000000052525aa5850100003a565aa58501000020bf02000000000088130000000000008813000005000000204e00000000000100010300a10f0432055800821a06000000000000000000000000000000000000",
                "id": 1
            }
        """.trimIndent()

        coEvery { httpClient.post(rpcURL, body, any(), any()) } returns jsonResponse

        val response = runBlocking {
            rpc.getJobRegistration(JobIdentifier(AccountId32(account.hexToBa()), script.hexToBa()))
        }

        assertEquals(script, response.script.toHex())
    }
}