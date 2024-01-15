package acurast.rpc

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.AccountId32
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.MultiOrigin
import acurast.rpc.engine.RpcEngine
import acurast.rpc.type.FrameSystemAccountInfo
import acurast.rpc.type.PalletAssetsAssetAccount
import io.mockk.*
import io.mockk.impl.annotations.MockK
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

class AcurastRpcTest {
    @MockK
    private lateinit var rpcEngine: RpcEngine<*>
    @MockK
    private lateinit var rpcExecutor: RpcEngine.Executor

    private lateinit var acurastRpc: AcurastRpc

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        acurastRpc = AcurastRpc(rpcEngine)
        coEvery { rpcEngine.executor(any()) } returns rpcExecutor
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

        coEvery { rpcExecutor.request(any(), any(), any()) } returns jsonResponse

        val response = runBlocking {
            acurastRpc.isAttested(account.hexToBa())
        }

        assertTrue(response)

        coVerify { rpcExecutor.request(body = matchJsonRpcRequest(method, params), timeout = any(), peek = any()) }
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

        coEvery { rpcExecutor.request(any(), any(), any()) } returns jsonResponse

        val response = runBlocking {
            acurastRpc.getAccountInfo(account.hexToBa())
        }

        assertEquals(expectedResponse, response)

        coVerify { rpcExecutor.request(body = matchJsonRpcRequest(method, params), timeout = any(), peek = any()) }
    }

    @Test
    fun `Get Account Asset Information`() {
        val assetId = 10
        val account = "0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"
        val method = "state_getStorage"
        val params = JSONArray().apply {
            put("682a59d51ab9e48a8c8cc418ff9708d2b99d880ec681799c0cf30e8886371da91523c4974e05c5b917b6037dec663b5d0a000000de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
        }

        val expectedResponse = PalletAssetsAssetAccount(
            balance = BigInteger("99999999999999999999999999000000000000"),
            isFrozen = false,
            reason = 0,
        )

        val jsonResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": "0x00f05a2b57218a097ac4865aa84c3b4b0000",
                "id": 1
            }
        """.trimIndent())

        coEvery { rpcExecutor.request(any(), any(), any()) } returns jsonResponse

        val response = runBlocking {
            acurastRpc.getAccountAssetInfo(assetId, account.hexToBa())
        }

        assertEquals(expectedResponse, response)

        coVerify { rpcExecutor.request(body = matchJsonRpcRequest(method, params), timeout = any(), peek = any()) }
    }

    @Test
    fun `Get Job Matches`() {
        val getKeysMethod = "state_getKeys"
        val getKeysParams = JSONArray().apply {
            put("1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d733e7c1e81d34082d8f34de585b45ce09353cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196")
        }

        val getKeysResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": [
                    "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d73bda717e2c1dbd94fc3adce6ba1ba9c0fd80a8b0d800a3320528693947f7317871b2d51e5f3c8f3d0d4e4f7e6938ed68f6417d15b337c54cc1c927a3d3125787500d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d02000000000000000000000000000000",
                ],
                "id": 1
            }
        """.trimIndent())

        coEvery {
            rpcExecutor.request(
                body = matchJsonRpcRequest(method = getKeysMethod, params = getKeysParams),
                timeout = any(),
                peek = any(),
            )
        } returns getKeysResponse

        val account = "53cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196"
        val queryStorageMethod = "state_queryStorageAt"
        val queryStorageParams = JSONArray().apply {
            put(JSONArray().apply {
                put("0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d73bda717e2c1dbd94fc3adce6ba1ba9c0fd80a8b0d800a3320528693947f7317871b2d51e5f3c8f3d0d4e4f7e6938ed68f6417d15b337c54cc1c927a3d3125787500d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d02000000000000000000000000000000")
            })
        }

        val queryStorageResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": [
                    {
                        "block": "0x40a35a0027b2dae57f8543212c55bae2cbf43a389fac70b304db73dc6f497b87",
                        "changes": [
                            [
                                "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d73bda717e2c1dbd94fc3adce6ba1ba9c0fd80a8b0d800a3320528693947f7317871b2d51e5f3c8f3d0d4e4f7e6938ed68f6417d15b337c54cc1c927a3d3125787500d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d02000000000000000000000000000000",
                                "0x00e803000000000000ea030000000000000000000000000000011e1c000000000000000000000000000008008402afe7b554c9bf483bff2e893c683cfdb8f1dcf17ef3b2e4d1067d48c8a50467aa018402ee37f55f791cf10d2fa2bb7c9743bd30226fdfe5364465b9903cf3eb159e98f5"
                            ],
                            [
                                "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d73bda717e2c1dbd94fc3adce6ba1ba9c0fd80a8b0d800a3320528693947f7317871b2d51e5f3c8f3d0d4e4f7e6938ed68f8eab5b223dc34c614476ea3d8bb6dd5900d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d03000000000000000000000000000000",
                                "0x00d007000000000000ea030000000000000000000000000000011e1c000000000000000000000000000008008402afe7b554c9bf483bff2e893c683cfdb8f1dcf17ef3b2e4d1067d48c8a50467aa018402ee37f55f791cf10d2fa2bb7c9743bd30226fdfe5364465b9903cf3eb159e98f5"
                            ]
                        ]
                    }
                ],
                "id": 1
            }
        """.trimIndent())

       coEvery {
           rpcExecutor.request(
               body = matchJsonRpcRequest(method = queryStorageMethod, params = queryStorageParams),
               timeout = any(),
               peek = any(),
           )
       } returns queryStorageResponse

        val response = runBlocking {
            acurastRpc.getAssignedJobs(account.hexToBa())
        }

        assertEquals(2, response.size)
        assertEquals(0, response[0].slot)
        assertEquals(1000, response[0].startDelay)
        assertEquals(BigInteger.valueOf(1002), response[0].feePerExecution.x)
        assertTrue(response[0].acknowledged)
    }

    @Test
    fun `Get Job Matches 2`() {
        val getKeysMethod = "state_getKeys"
        val getKeysParams = JSONArray().apply {
            put("1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d733e7c1e81d34082d8f34de585b45ce09353cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196")
        }

        val getKeysResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": [
                    "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d73d09150b9053d7f4684e2946402e967ac998d2cf8c0590b83951070633f0ff21fe613fae2c18ed2db69b8404f66f4311578dfc438585deb389231ab96e2c8beeb009a1a0c52c2d7f23820caa7757acf049e07fcb68015919638feece41a4b0ee538df000000000000000000000000000000",
                ],
                "id": 1
            }
        """.trimIndent())

        coEvery {
            rpcExecutor.request(
                body = matchJsonRpcRequest(method = getKeysMethod, params = getKeysParams),
                timeout = any(),
                peek = any(),
            )
        } returns getKeysResponse

        val account = "53cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196"
        val queryStorageMethod = "state_queryStorageAt"
        val queryStorageParams = JSONArray().apply {
            put(JSONArray().apply {
                put("0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d73d09150b9053d7f4684e2946402e967ac998d2cf8c0590b83951070633f0ff21fe613fae2c18ed2db69b8404f66f4311578dfc438585deb389231ab96e2c8beeb009a1a0c52c2d7f23820caa7757acf049e07fcb68015919638feece41a4b0ee538df000000000000000000000000000000")
            })
        }

        val queryStorageResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": [
                    {
                        "block": "0x867a3c35db829c60018b055e85b4b326300e700688f5e5e09960e952428e03dc",
                        "changes": [
                            [
                                "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d73d09150b9053d7f4684e2946402e967ac998d2cf8c0590b83951070633f0ff21fe613fae2c18ed2db69b8404f66f4311578dfc438585deb389231ab96e2c8beeb009a1a0c52c2d7f23820caa7757acf049e07fcb68015919638feece41a4b0ee538df000000000000000000000000000000",
                                "0x030000000000000000df273a77000000000000000000000000001900000000000000000000000000000000"
                            ]
                        ]
                    }
                ],
                "id": 1
            }
        """.trimIndent())

        coEvery {
            rpcExecutor.request(
                body = matchJsonRpcRequest(method = queryStorageMethod, params = queryStorageParams),
                timeout = any(),
                peek = any(),
            )
        } returns queryStorageResponse

        val response = runBlocking {
            acurastRpc.getAssignedJobs(account.hexToBa())
        }

        assertEquals(1, response.size)
        assertEquals(3, response[0].slot)
        assertEquals(0, response[0].startDelay)
        assertEquals(BigInteger.valueOf(2000299999), response[0].feePerExecution.x)
        assertTrue(!response[0].acknowledged)
    }

    @Test
    fun `Get Job Matches 3`() {
        val getKeysMethod = "state_getKeys"
        val getKeysParams = JSONArray().apply {
            put("1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d733e7c1e81d34082d8f34de585b45ce09353cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196")
        }

        val getKeysResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": [
                    "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d7356e3d8d388a039de16da63e7231ac9839717c7ea2bb68aec76fdd8c6761490939f721d5719d42f80f13529cd69be59261072337b97a36a2853d61ae29668469d00100b4cf29c67f598e147c5481bba5d332ca37256df9a9984c58ca30fb3624e526e000000000000000000000000000000",
                    "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d7356e3d8d388a039de16da63e7231ac9839717c7ea2bb68aec76fdd8c6761490939f721d5719d42f80f13529cd69be592678dfc438585deb389231ab96e2c8beeb009a1a0c52c2d7f23820caa7757acf049e07fcb68015919638feece41a4b0ee538df000000000000000000000000000000"
                ],
                "id": 1
            }
        """.trimIndent())

        coEvery {
            rpcExecutor.request(
                body = matchJsonRpcRequest(method = getKeysMethod, params = getKeysParams),
                timeout = any(),
                peek = any(),
            )
        } returns getKeysResponse

        val account = "53cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196"
        val queryStorageMethod = "state_queryStorageAt"
        val queryStorageParams = JSONArray().apply {
            put(JSONArray().apply {
                put("0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d7356e3d8d388a039de16da63e7231ac9839717c7ea2bb68aec76fdd8c6761490939f721d5719d42f80f13529cd69be59261072337b97a36a2853d61ae29668469d00100b4cf29c67f598e147c5481bba5d332ca37256df9a9984c58ca30fb3624e526e000000000000000000000000000000")
                put("0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d7356e3d8d388a039de16da63e7231ac9839717c7ea2bb68aec76fdd8c6761490939f721d5719d42f80f13529cd69be592678dfc438585deb389231ab96e2c8beeb009a1a0c52c2d7f23820caa7757acf049e07fcb68015919638feece41a4b0ee538df000000000000000000000000000000")
            })
        }

        val queryStorageResponse = JSONObject("""                	
            {
                "jsonrpc": "2.0",
                "result": [
                    {
                        "block": "0x867a3c35db829c60018b055e85b4b326300e700688f5e5e09960e952428e03dc",
                        "changes": [
                            [
                                "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d7356e3d8d388a039de16da63e7231ac9839717c7ea2bb68aec76fdd8c6761490939f721d5719d42f80f13529cd69be59261072337b97a36a2853d61ae29668469d00100b4cf29c67f598e147c5481bba5d332ca37256df9a9984c58ca30fb3624e526e000000000000000000000000000000",
                                "0x000000000000000000c1270900000000000000000000000000000100000000000000000000000000000000"
                            ],
                            [
                                "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d7356e3d8d388a039de16da63e7231ac9839717c7ea2bb68aec76fdd8c6761490939f721d5719d42f80f13529cd69be592678dfc438585deb389231ab96e2c8beeb009a1a0c52c2d7f23820caa7757acf049e07fcb68015919638feece41a4b0ee538df000000000000000000000000000000",
                                "0x020000000000000000df273a77000000000000000000000000001900000000000000000000000000000000"
                            ]
                        ]
                    }
                ],
                "id": 1
            }
        """.trimIndent())

        coEvery {
            rpcExecutor.request(
                body = matchJsonRpcRequest(method = queryStorageMethod, params = queryStorageParams),
                timeout = any(),
                peek = any(),
            )
        } returns queryStorageResponse

        val response = runBlocking {
            acurastRpc.getAssignedJobs(account.hexToBa())
        }

        assertEquals(2, response.size)
        assertEquals(2, response[1].slot)
        assertEquals(0, response[0].startDelay)
        assertEquals(BigInteger.valueOf(600001), response[0].feePerExecution.x)
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
                "result": "d4697066733a2f2f516d516b6d7234576a3772666e4232445a45565a67474d58566b6f4a684d4d6e5a784e336a733565693542514353010453cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196013075000000000000592103e08601000059740ae086010000187900000000000000000000000000000100000001000000010000000000000100010300a10f0432055800821a060000010453cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196000000000000000000000000000000000000000000000000",
                "id": 1
            }
        """.trimIndent())

        coEvery {
            rpcExecutor.request(
                body = matchJsonRpcRequest(method, params),
                timeout = any(),
                peek = any(),
            )
        } returns jsonResponse

        val response = runBlocking {
            acurastRpc.getJobRegistration(JobIdentifier(MultiOrigin.Acurast(AccountId32(account.hexToBa())), BigInteger.ONE))
        }

        assertEquals(script, response.script.toHex())
    }
}