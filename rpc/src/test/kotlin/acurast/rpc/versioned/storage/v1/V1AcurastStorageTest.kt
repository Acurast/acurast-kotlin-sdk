package acurast.rpc.versioned.storage.v1

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.AccountId32
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.MultiOrigin
import acurast.rpc.engine.RpcEngine
import acurast.rpc.pallet.State
import acurast.rpc.type.FrameSystemAccountInfo
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
import org.junit.jupiter.api.Assertions.assertTrue
import java.math.BigInteger
import kotlin.test.assertEquals

class V1AcurastStorageTest {
    @MockK
    private lateinit var rpcEngine: RpcEngine

    private lateinit var acurastStorage: V1AcurastStorage

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        acurastStorage = V1AcurastStorage(rpcEngine, State())
    }

    @After
    fun clean() {
        unmockkAll()
    }

    @Test
    fun `Verify if a given account is attested`() {
        val account = "0x58aee56e2857bde581bb6fe383de7fb7fac7e6c13ab54952b9d1bfec9af7ee00"
        val method = "state_getStorage"
        val params = JSONArray().apply {
            put("d8f45172ad1e7575680eaa8157102800f75db0c33624f81fd193acdc07620654f9f1bd810967c8bb5367f6626d3c7c0c58aee56e2857bde581bb6fe383de7fb7fac7e6c13ab54952b9d1bfec9af7ee00")
        }

        val jsonResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": "0x08d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d8eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48",
                "id": 1
            }
        """.trimIndent())

        coEvery { rpcEngine.request(any(), any()) } returns jsonResponse

        val response = runBlocking {
            acurastStorage.isAttested(account.hexToBa())
        }

        assertTrue(response)

        coVerify { rpcEngine.request(body = matchJsonRpcRequest(method, params), timeout = any()) }
    }

    @Test
    fun `Get Account Information`() {
        val account = "0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"
        val method = "state_getStorage"
        val params = JSONArray().apply {
            put("26aa394eea5630e07c48ae0c9558cef7b99d880ec681799c0cf30e8886371da9de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
        }

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

        val jsonResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": "0x0000000001000000010000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                "id": 1
            }
        """.trimIndent())

        coEvery { rpcEngine.request(any(), any()) } returns jsonResponse

        val response = runBlocking {
            acurastStorage.getAccountInfo(account.hexToBa())
        }

        assertEquals(expectedResponse, response)

        coVerify { rpcEngine.request(body = matchJsonRpcRequest(method, params), timeout = any()) }
    }

    @Test
    fun `Get Job Matches`() {
        val getKeysMethod = "state_getKeys"
        val getKeysParams = JSONArray().apply {
            put("1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d733e8f5a0c6e0343966d8dcb14320562fb64198257facae49d78f5bf09b7513d6e0e5de038b79dc14b672b1253164cbb1b")
        }

        val getKeysResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": [
                    "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d733e8f5a0c6e0343966d8dcb14320562fb64198257facae49d78f5bf09b7513d6e0e5de038b79dc14b672b1253164cbb1b129bfa4ce84324b3136379efd27edfff00185a8b5f92ecd348ed9b12a047ca2b28488b1398065a8dff8dcf886245f9280b69000000000000000000000000000000",
                ],
                "id": 1
            }
        """.trimIndent())

        coEvery {
            rpcEngine.request(
                body = matchJsonRpcRequest(method = getKeysMethod, params = getKeysParams),
                timeout = any(),
            )
        } returns getKeysResponse

        // 5EKxDut4LsXUyCTdvG8jTj7SDxcwMY3RiAoyNgLhchkYd3Gg
        val account = "64198257facae49d78f5bf09b7513d6e0e5de038b79dc14b672b1253164cbb1b"
        val queryStorageMethod = "state_queryStorageAt"
        val queryStorageParams = JSONArray().apply {
            put(JSONArray().apply {
                put("0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d733e8f5a0c6e0343966d8dcb14320562fb64198257facae49d78f5bf09b7513d6e0e5de038b79dc14b672b1253164cbb1b129bfa4ce84324b3136379efd27edfff00185a8b5f92ecd348ed9b12a047ca2b28488b1398065a8dff8dcf886245f9280b69000000000000000000000000000000")
            })
        }

        val queryStorageResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": [
                    {
                        "block": "0x53a0e9a4db4d857fe9bf995bbf62f105b5732fec2efaec91e206fb172dd57632",
                        "changes": [
                            [
                                "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d73d09150b9053d7f4684e2946402e967ac998d2cf8c0590b83951070633f0ff21fe613fae2c18ed2db69b8404f66f4311578dfc438585deb389231ab96e2c8beeb009a1a0c52c2d7f23820caa7757acf049e07fcb68015919638feece41a4b0ee538df000000000000000000000000000000",
                                "0x000000000000000000f495357700000000000000000000000000020000000000000000000000000000000000"
                            ]
                        ]
                    }
                ],
                "id": 1
            }
        """.trimIndent())

        coEvery {
            rpcEngine.request(
                body = matchJsonRpcRequest(method = queryStorageMethod, params = queryStorageParams),
                timeout = any(),
            )
        } returns queryStorageResponse

        val response = runBlocking {
            acurastStorage.getAssignedJobs(account.hexToBa())
        }

        assertEquals(1, response.size)
        assertEquals(0, response[0].slot)
        assertEquals(0, response[0].startDelay)
        assertEquals(BigInteger.valueOf(2000000500), response[0].feePerExecution.x)
        assertTrue(!response[0].acknowledged)
    }

    @Test
    fun `Get Job Registration`() {
        val account = "1cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c"
        val script = "697066733a2f2f516d516b6d7234576a3772666e4232445a45565a67474d58566b6f4a684d4d6e5a784e336a733565693542514353"

        val method = "state_getStorage"
        val params = JSONArray().apply {
            put("d8f45172ad1e7575680eaa8157102800f86e394d601279aa535d660453ff8e0928c68911e8cc8866ccf20f186a8f202b001cbd2d43530a44705ad088af313e18f80b53ef16b36177cd4b77b846f2a5f07c3ba80a3778f04ebf45e806d19a05202501000000000000000000000000000000")
        }

        val jsonResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": "d4697066733a2f2f516d516b6d7234576a3772666e4232445a45565a67474d58566b6f4a684d4d6e5a784e336a733565693542514353010453cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196013075000000000000592103e08601000059740ae086010000187900000000000000000000000000000100000001000000010000000000000100010300a10f0432055800821a060000010453cf73c65e36ec0bf3d7539780e83f0000",
                "id": 1
            }
        """.trimIndent())

        coEvery {
            rpcEngine.request(
                body = matchJsonRpcRequest(method, params),
                timeout = any(),
            )
        } returns jsonResponse

        val response = runBlocking {
            acurastStorage.getJobRegistration(JobIdentifier(MultiOrigin.Acurast(AccountId32(account.hexToBa())), BigInteger.ONE))
        }

        assertEquals(script, response?.script?.toHex())
    }
}