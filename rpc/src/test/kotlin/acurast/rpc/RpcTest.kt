package acurast.rpc

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.AccountId32
import acurast.codec.type.Fungibility
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.MultiOrigin
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
import org.junit.jupiter.api.Assertions.assertTrue
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
    fun `Verify if a given account is attested`() {
        val account = "0x58aee56e2857bde581bb6fe383de7fb7fac7e6c13ab54952b9d1bfec9af7ee00"
        val param = JSONArray().put("d8f45172ad1e7575680eaa8157102800f75db0c33624f81fd193acdc07620654f9f1bd810967c8bb5367f6626d3c7c0c58aee56e2857bde581bb6fe383de7fb7fac7e6c13ab54952b9d1bfec9af7ee00")
        val body = JSONRequest("state_getStorage", param).toString()

        val jsonResponse = """                	
            {
                "jsonrpc": "2.0",
                "result": "0x08d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d8eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48",
                "id": 1
            }
        """.trimIndent()

        coEvery { httpClient.post(any(), any(), any(), any()) } returns jsonResponse

        val response = runBlocking {
            rpc.isAttested(account.hexToBa())
        }

        assertTrue(response)

        coVerify { httpClient.post(rpcURL, body = body ) }
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
        val paramGetKeys = "1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d733e7c1e81d34082d8f34de585b45ce09353cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196"
        val bodyGetKeys = JSONRequest("state_getKeys", JSONArray().put(paramGetKeys)).toString()

        val getKeysResponse = """                	
            {
                "jsonrpc": "2.0",
                "result": [
                    "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d733e7c1e81d34082d8f34de585b45ce09353cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f219658239c5938c7ca44e8e83dd55541eb7f001cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c01000000000000000000000000000000"
                ],
                "id": 1
            }
        """.trimIndent()

        coEvery { httpClient.post(any(), bodyGetKeys, any(), any(), any(), any()) } returns getKeysResponse

        val account = "53cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196"
        val param = JSONArray().put("0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d733e7c1e81d34082d8f34de585b45ce09353cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f219658239c5938c7ca44e8e83dd55541eb7f001cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c01000000000000000000000000000000")
        val body = JSONRequest("state_queryStorageAt", JSONArray().put(param)).toString()

        val jsonResponse = """                	
            {
                "jsonrpc": "2.0",
                "result": [
                    {
                        "block": "0x40a35a0027b2dae57f8543212c55bae2cbf43a389fac70b304db73dc6f497b87",
                        "changes": [
                            [
                                "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d733e7c1e81d34082d8f34de585b45ce09353cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f219658239c5938c7ca44e8e83dd55541eb7f001cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c01000000000000000000000000000000",
                                "0x00000000000000000000010300a10f0432055800cad401000110000000000000000000000000000000"
                            ],
                            [
                                "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d733e7c1e81d34082d8f34de585b45ce09353cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f219658239c5938c7ca44e8e83dd55541eb7f001cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c01000000000000000000000000000000",
                                "0x00000000000000000000010300a10f0432055800cad401000110000000000000000000000000000000"
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
        assertEquals(0, response[0].slot)
        assertEquals(BigInteger.ZERO, response[0].startDelay)
        assertEquals(Fungibility.Kind.Fungible, response[0].feePerExecution.fungibility.kind)
        assertEquals(BigInteger.valueOf(30_002), response[0].feePerExecution.fungibility.amount)
        assertTrue(response[0].acknowledged)
    }

    @Test
    fun `Get Job Registration`() {
        val account = "1cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c"
        val script = "697066733a2f2f516d516b6d7234576a3772666e4232445a45565a67474d58566b6f4a684d4d6e5a784e336a733565693542514353"

        val param = JSONArray().put("d8f45172ad1e7575680eaa8157102800f86e394d601279aa535d660453ff8e0928c68911e8cc8866ccf20f186a8f202b001cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c3ba80a3778f04ebf45e806d19a05202501000000000000000000000000000000")
        val body = JSONRequest("state_getStorage", param).toString()

        val jsonResponse = """                	
            {
                "jsonrpc": "2.0",
                "result": "d4697066733a2f2f516d516b6d7234576a3772666e4232445a45565a67474d58566b6f4a684d4d6e5a784e336a733565693542514353010453cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196013075000000000000592103e08601000059740ae086010000187900000000000000000000000000000100000001000000010000000000000100010300a10f0432055800821a060000010453cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196000000000000000000000000000000000000000000000000",
                "id": 1
            }
        """.trimIndent()

        coEvery { httpClient.post(rpcURL, body, any(), any()) } returns jsonResponse

        val response = runBlocking {
            rpc.getJobRegistration(JobIdentifier(MultiOrigin.Acurast(AccountId32(account.hexToBa())), BigInteger.ONE))
        }

        assertEquals(script, response.script.toHex())
    }
}