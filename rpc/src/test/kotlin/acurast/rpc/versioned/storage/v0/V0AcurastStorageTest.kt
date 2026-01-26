package acurast.rpc.versioned.storage.v0

import acurast.codec.extensions.hexToBa
import acurast.codec.extensions.toHex
import acurast.codec.type.AccountId32
import acurast.codec.type.AccountInfo
import acurast.codec.type.AccountOverview
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.MultiOrigin
import acurast.codec.type.compute.Commitment
import acurast.codec.type.compute.CommitmentWeights
import acurast.codec.type.compute.Delegation
import acurast.codec.type.compute.MemoryBuffer
import acurast.codec.type.compute.PoolReward
import acurast.codec.type.compute.Stake
import acurast.rpc.engine.RpcEngine
import acurast.rpc.pallet.State
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
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class V0AcurastStorageTest {
    @MockK
    private lateinit var rpcEngine: RpcEngine

    private lateinit var acurastStorage: V0AcurastStorage

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        acurastStorage = V0AcurastStorage(rpcEngine, State())
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

        val expectedResponse = AccountInfo(
            nonce = 0U,
            consumers = 1U,
            providers = 1U,
            sufficients = 0U,
            data = AccountInfo.Data(
                free = BigInteger("1152921504606846976"),
                reserved = BigInteger.ZERO,
                frozen = BigInteger.ZERO,
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
            rpcEngine.request(
                body = matchJsonRpcRequest(method = getKeysMethod, params = getKeysParams),
                timeout = any(),
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
           rpcEngine.request(
               body = matchJsonRpcRequest(method = queryStorageMethod, params = queryStorageParams),
               timeout = any(),
           )
       } returns queryStorageResponse

        val response = runBlocking {
            acurastStorage.getAssignedJobs(account.hexToBa())
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
            rpcEngine.request(
                body = matchJsonRpcRequest(method = getKeysMethod, params = getKeysParams),
                timeout = any(),
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
            rpcEngine.request(
                body = matchJsonRpcRequest(method = queryStorageMethod, params = queryStorageParams),
                timeout = any(),
            )
        } returns queryStorageResponse

        val response = runBlocking {
            acurastStorage.getAssignedJobs(account.hexToBa())
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
            rpcEngine.request(
                body = matchJsonRpcRequest(method = getKeysMethod, params = getKeysParams),
                timeout = any(),
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
            rpcEngine.request(
                body = matchJsonRpcRequest(method = queryStorageMethod, params = queryStorageParams),
                timeout = any(),
            )
        } returns queryStorageResponse

        val response = runBlocking {
            acurastStorage.getAssignedJobs(account.hexToBa())
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
                "result": "d4697066733a2f2f516d516b6d7234576a3772666e4232445a45565a67474d58566b6f4a684d4d6e5a784e336a733565693542514353010453cf73c65e36ec0bf3d7539780e83febd2d1b01de0df4f6bb7a95157715f2196013075000000000000592103e08601000059740ae086010000187900000000000000000000000000000100000001000000010000000000000",
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

    @Test
    fun `Get Account Overview`() {
        val account = "b64876eed630b7cfdb23ae4e4a92c311a322a871c04b99675e8d4a97286c7337"

        coEvery {
            rpcEngine.request(
                body = matchJsonRpcRequest("state_getKeys", JSONArray().apply {
                    put("56c55973ec96db3a5ef84bbd48553aebf6eed5c65f50198f0f53f8f499f770911c5c70a136a95466b64876eed630b7cfdb23ae4e4a92c311a322a871c04b99675e8d4a97286c7337")
                }),
                timeout = any(),
            )
        } returns JSONObject("""
            {
                "jsonrpc": "2.0",
                "id": "1",
                "result": ["0x56c55973ec96db3a5ef84bbd48553aebf6eed5c65f50198f0f53f8f499f770911c5c70a136a95466b64876eed630b7cfdb23ae4e4a92c311a322a871c04b99675e8d4a97286c73370a030000000000000000000000000000"]
            }
        """.trimIndent())

        coEvery {
            rpcEngine.request(
                body = matchJsonRpcRequest("state_queryStorageAt", JSONArray().apply {
                    put(JSONArray().apply {
                        put("26aa394eea5630e07c48ae0c9558cef7b99d880ec681799c0cf30e8886371da9fe5bf4412f396fb075de00f868a22462b64876eed630b7cfdb23ae4e4a92c311a322a871c04b99675e8d4a97286c7337")
                        put("5e8a19e3cd1b7c148b33880c479c0281b99d880ec681799c0cf30e8886371da9fe5bf4412f396fb075de00f868a22462b64876eed630b7cfdb23ae4e4a92c311a322a871c04b99675e8d4a97286c73373ba80a3778f04ebf45e806d19a05202501000000000000000000000000000000")
                        put("0x56c55973ec96db3a5ef84bbd48553aebf6eed5c65f50198f0f53f8f499f770911c5c70a136a95466b64876eed630b7cfdb23ae4e4a92c311a322a871c04b99675e8d4a97286c73370a030000000000000000000000000000")
                    })
                }),
                timeout = any(),
            )
        } returns JSONObject("""
            {
                "jsonrpc": "2.0",
                "id": "1",
                "result": [{"block":"0xebceed8cd1d378569d732b8e50cfdd941fb318cded91c7306a05dc9be4f91a39","changes":[["0x26aa394eea5630e07c48ae0c9558cef7b99d880ec681799c0cf30e8886371da9fe5bf4412f396fb075de00f868a22462b64876eed630b7cfdb23ae4e4a92c311a322a871c04b99675e8d4a97286c7337","0x6a0300000100000001000000000000008f2e2d28c45002000000000000000000d63e63053700000000000000000000000090c3dc53480100000000000000000000000000000000000000000000000080"],["0x5e8a19e3cd1b7c148b33880c479c0281b99d880ec681799c0cf30e8886371da9fe5bf4412f396fb075de00f868a22462b64876eed630b7cfdb23ae4e4a92c311a322a871c04b99675e8d4a97286c73373ba80a3778f04ebf45e806d19a05202501000000000000000000000000000000",null],["0x56c55973ec96db3a5ef84bbd48553aebf6eed5c65f50198f0f53f8f499f770911c5c70a136a95466b64876eed630b7cfdb23ae4e4a92c311a322a871c04b99675e8d4a97286c73370a030000000000000000000000000000","0x0090c3dc5348010000000000000000000090c3dc534801000000000000000000df438800807000000000000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000090c3dc534801000000000000000000000000000000000000000000000000000090c3dc53480100000000000000000000000000000000000000000000000000dd2c1e7c6e010000000000000000000000000000000000000000000000000000"]]}]
            }
        """.trimIndent())

        coEvery {
            rpcEngine.request(
                body = matchJsonRpcRequest("state_queryStorageAt", JSONArray().apply {
                    put(JSONArray().apply {
                        put("56c55973ec96db3a5ef84bbd48553aebca407206ec1ab726b2636c4b145ac2870a030000000000000000000000000000")
                    })
                }),
                timeout = any(),
            )
        } returns JSONObject("""
            {
                "jsonrpc": "2.0",
                "id": "1",
                "result": [{"block":"0xebceed8cd1d378569d732b8e50cfdd941fb318cded91c7306a05dc9be4f91a39","changes":[["0x56c55973ec96db3a5ef84bbd48553aebca407206ec1ab726b2636c4b145ac2870a030000000000000000000000000000","0x010080145e6e2f2e0000000000000000000080145e6e2f2e000000000000000000a9a986008070000000301c0202c5540000000000000000000000000000000000000000000000000000006e58f8ca880a0000000000000000000000000000000000000000000000000000000000002680c85e0ab789010000000000000000260023f8d450c600000000000000000001c12600000080145e6e2f2e000000000000000000000000000000000000000000000000000080145e6e2f2e0000000000000000000000000000000000000000000000000026705f1b8108c50000000000000000000000000000000000000000000000000026f00482b66e8801000000000000000000000000000000000000000000000000c22600000080145e6e2f2e000000000000000000000000000000000000000000000000000080145e6e2f2e00000000000000000000000000000000000000000000000000260023f8d450c6000000000000000000000000000000000000000000000000002680c85e0ab78901000000000000000000000000000000000000000000000000016c2d7a00d3a9de1531849c4fcd83e68e000000000000000000000000000000000000000094b31d768524024f4116459c0000000000000000000000000000000000000000a9a986000c6cf1b57f432021cfc4c90f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c626000042260000"]]}]
            }
        """.trimIndent())

        val accountOverview = runBlocking {
            acurastStorage.getAccountOverview(account.hexToBa())
        }

        assertEquals(
            AccountOverview(
                accountInfo = AccountInfo(
                    nonce = 874U,
                    consumers = 1U,
                    providers = 1U,
                    sufficients = 0U,
                    data = AccountInfo.Data(
                        free = BigInteger("651753371283087"),
                        reserved = BigInteger("236313591510"),
                        frozen = BigInteger("361000000000000"),
                    ),
                ),
                commitment = null,
                delegations = listOf(
                    Delegation(
                        stake = Stake(
                            amount = BigInteger("361000000000000"),
                            rewardableAmount = BigInteger("361000000000000"),
                            created = 8_930_271U,
                            cooldownPeriod = 28_800U,
                            cooldownStarted = null,
                            accruedReward = BigInteger("0"),
                            accruedSlash = BigInteger("0"),
                            allowAutoCompound = true,
                            paid = BigInteger("0"),
                            appliedSlash = BigInteger("0"),
                        ),
                        rewardWeight = BigInteger("361000000000000"),
                        slashWeight = BigInteger("361000000000000"),
                        rewardDebt = BigInteger("1574040382685"),
                        slashDebt = BigInteger("0"),
                    ) to Commitment(
                        stake = Stake(
                            amount = BigInteger("13000000000000000"),
                            rewardableAmount = BigInteger("13000000000000000"),
                            created = 8_825_257U,
                            cooldownPeriod = 28_800U,
                            cooldownStarted = null,
                            accruedReward = BigInteger("93205118983216"),
                            accruedSlash = BigInteger("0"),
                            allowAutoCompound = false,
                            paid = BigInteger("11582637103214"),
                            appliedSlash = BigInteger("0"),
                        ),
                        commission = BigDecimal.valueOf(0, 9), // 0.0
                        delegationsTotalAmount = BigInteger("110820921015042086"),
                        delegationsTotalRewardableAmount = BigInteger("55820921015042086"),
                        weights = MemoryBuffer(
                            past = Pair(9_921U, CommitmentWeights(
                                selfRewardWeight = BigInteger("13000000000000000"),
                                selfSlashWeight = BigInteger("13000000000000000"),
                                delegationsRewardWeight = BigInteger("55459921015042086"),
                                delegationsSlashWeight = BigInteger("110459921015042086"),
                            )),
                            current = Pair(9_922U, CommitmentWeights(
                                selfRewardWeight = BigInteger("13000000000000000"),
                                selfSlashWeight = BigInteger("13000000000000000"),
                                delegationsRewardWeight = BigInteger("55820921015042086"),
                                delegationsSlashWeight = BigInteger("110820921015042086"),
                            )),
                        ),
                        poolRewards = MemoryBuffer(
                            past = Pair(8_007_020U, PoolReward(
                                rewardPerWeight = BigInteger("44225546750470760874800753107"),
                                slashPerWeight = BigInteger("48363182510477381092016763796"),
                            )),
                            current = Pair(8_825_257U, PoolReward(
                                rewardPerWeight = BigInteger("4886198641756364221625494540"),
                                slashPerWeight = BigInteger("0"),
                            )),
                        ),
                        lastScoringEpoch = 9_926U,
                        lastSlashingEpoch = 9_794U,
                    ),
                ),
            ),
            accountOverview,
        )
    }

    @Test
    fun `Storage Query Acurast_StoredAttestation`() {
        val key = Acurast_StoredAttestation("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())

        assertContentEquals(
            "0xd8f45172ad1e7575680eaa8157102800f75db0c33624f81fd193acdc07620654de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query Acurast_StoredJobRegistration`() {
        val key = Acurast_StoredJobRegistration(
            JobIdentifier(
                origin = MultiOrigin.Acurast(AccountId32("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())),
                id = BigInteger.ZERO,
            ),
        )

        assertContentEquals(
            "0xd8f45172ad1e7575680eaa8157102800f86e394d601279aa535d660453ff8e09ff699fe6ae26ef97168ddeaecbc34ce300d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d463be1d58a72e9618ea59884367c435800000000000000000000000000000000".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query Acurast_ExecutionEnvironment`() {
        val key = Acurast_ExecutionEnvironment(
            JobIdentifier(
                origin = MultiOrigin.Acurast(AccountId32("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())),
                id = BigInteger.ZERO,
            ),
            accountId = "0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa(),
        )

        assertContentEquals(
            "0xd8f45172ad1e7575680eaa81571028003b13796cad693cd980d0f24b9ff4cbfaea6c62216b6983bf26def5438482d4ba00d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d00000000000000000000000000000000de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastProcessorManager_ProcessorToManagerIdIndex`() {
        val key = AcurastProcessorManager_ProcessorToManagerIdIndex("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())

        assertContentEquals(
            "0xc698b31ad72d251a494de23e0dd66846f3a728ad8efdeffd18f039142abe31b2de1e86a9a8c739864cf3cc5ec2bea59f".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastProcessorManager_ManagerCounter`() {
        val key = AcurastProcessorManager_ManagerCounter("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())

        assertContentEquals(
            "0xc698b31ad72d251a494de23e0dd6684652af9a5a36f7ce16cc96808f97171a0bde1e86a9a8c739864cf3cc5ec2bea59f".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastProcessorManager_ManagementEndpoint`() {
        val key = AcurastProcessorManager_ManagementEndpoint(BigInteger.ZERO)

        assertContentEquals(
            "0xc698b31ad72d251a494de23e0dd66846baf48d86cb51dd4c22ca340c99164aaa463be1d58a72e9618ea59884367c435800000000000000000000000000000000".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastProcessorManager_ProcessorMigrationData`() {
        val key = AcurastProcessorManager_ProcessorMigrationData("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())

        assertContentEquals(
            "0xc698b31ad72d251a494de23e0dd66846c47b168bf86f71f46d2cc03079ff1ae4de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastProcessorManager_ProcessorHeartbeat`() {
        val key = AcurastProcessorManager_ProcessorHeartbeat("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())

        assertContentEquals(
            "0xc698b31ad72d251a494de23e0dd6684690ede7a772410f47186f66100c0bf9ecde1e86a9a8c739864cf3cc5ec2bea59f".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastMarketplace_StoredMatches`() {
        val key = AcurastMarketplace_StoredMatches(
            accountId = "0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa(),
            jobIdentifier = JobIdentifier(
                origin = MultiOrigin.Acurast(AccountId32("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())),
                id = BigInteger.ZERO,
            ),
        )

        assertContentEquals(
            "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d73de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27dea6c62216b6983bf26def5438482d4ba00d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d00000000000000000000000000000000".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastMarketplace_StoredMatches partial`() {
        val key = AcurastMarketplace_StoredMatches("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())

        assertContentEquals(
            "0x1aee6710ac79060b1e13291ba85112af2b949d1a72012eeaa1f6b481830d0d73de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastMarketplace_JobKeyIds`() {
        val key = AcurastMarketplace_JobKeyIds(
            JobIdentifier(
                origin = MultiOrigin.Acurast(AccountId32("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())),
                id = BigInteger.ZERO,
            ),
        )

        assertContentEquals(
            "0x1aee6710ac79060b1e13291ba85112afaf69a8f6416d983b30440e56737bd8f5ea6c62216b6983bf26def5438482d4ba00d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d00000000000000000000000000000000".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastMarketplace_AssignedProcessors`() {

    }

    @Test
    fun `Storage Query AcurastMarketplace_AssignedProcessors partial`() {

    }

    @Test
    fun `Storage Query AcurastMarketplace_StoredAdvertisementPricing`() {
        val key = AcurastMarketplace_StoredAdvertisementPricing("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())

        assertContentEquals(
            "0x1aee6710ac79060b1e13291ba85112af7343c819ecdbbce7ea9c22c60dd68245de1e86a9a8c739864cf3cc5ec2bea59f".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastMarketplace_StoredAdvertisementRestriction`() {
        val key = AcurastMarketplace_StoredAdvertisementRestriction("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())

        assertContentEquals(
            "0x1aee6710ac79060b1e13291ba85112af1d44a43f698cab34946cf754d09c275ade1e86a9a8c739864cf3cc5ec2bea59f".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query System_Account`() {
        val key = System_Account("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())

        assertContentEquals(
            "0x26aa394eea5630e07c48ae0c9558cef7b99d880ec681799c0cf30e8886371da9de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query Uniques_Account`() {
        val key = Uniques_Account("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa(), 0)

        assertContentEquals(
            "0x5e8a19e3cd1b7c148b33880c479c0281b99d880ec681799c0cf30e8886371da9de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d463be1d58a72e9618ea59884367c435800000000000000000000000000000000".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query Uniques_Asset`() {
        val key = Uniques_Asset(0, BigInteger.ZERO)

        assertContentEquals(
            "0x5e8a19e3cd1b7c148b33880c479c0281d34371a193a751eea5883e9553457b2e463be1d58a72e9618ea59884367c435800000000000000000000000000000000463be1d58a72e9618ea59884367c435800000000000000000000000000000000".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastCompute_Commitments`() {
        val key = AcurastCompute_Commitments(BigInteger.ZERO)

        assertContentEquals(
            "0x56c55973ec96db3a5ef84bbd48553aebca407206ec1ab726b2636c4b145ac28700000000000000000000000000000000".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastCompute_Delegations`() {
        val key = AcurastCompute_Delegations("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa(), BigInteger.ZERO)

        assertContentEquals(
            "0x56c55973ec96db3a5ef84bbd48553aebf6eed5c65f50198f0f53f8f499f77091518366b5b1bc7c99d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d00000000000000000000000000000000".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastCompute_Delegations partial`() {
        val key = AcurastCompute_Delegations("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())

        assertContentEquals(
            "0x56c55973ec96db3a5ef84bbd48553aebf6eed5c65f50198f0f53f8f499f77091518366b5b1bc7c99d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastCompute_MetricPoolLookup`() {
        val key = AcurastCompute_MetricPoolLookup("metric_pool_____________")

        assertContentEquals(
            "0x56c55973ec96db3a5ef84bbd48553aebe72eacd0a9cd6eeac082faf837bb0e1a6d65747269635f706f6f6c5f5f5f5f5f5f5f5f5f5f5f5f5f".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastCompute_MetricPoolLookup partial`() {
        val key = AcurastCompute_MetricPoolLookup()

        assertContentEquals(
            "0x56c55973ec96db3a5ef84bbd48553aebe72eacd0a9cd6eeac082faf837bb0e1a".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastCompute_MetricPools`() {
        val key = AcurastCompute_MetricPools(0)

        assertContentEquals(
            "0x56c55973ec96db3a5ef84bbd48553aebf8721c64ae517455ab3aaead1f107f7900".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastCompute_MetricPools partial`() {
        val key = AcurastCompute_MetricPools()

        assertContentEquals(
            "0x56c55973ec96db3a5ef84bbd48553aebf8721c64ae517455ab3aaead1f107f79".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query AcurastTokenConversion_LockedConversion`() {
        val key = AcurastTokenConversion_LockedConversion("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())

        assertContentEquals(
            "0x668e0c9d8f6a62868eb8af40bb5b05b755c0f3d7e41584c39a4afabe7b681593de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa(),
            key,
        )
    }

    @Test
    fun `Storage Query Vesting_Vesting`() {
        val key = Vesting_Vesting("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa())

        assertContentEquals(
            "0x5f27b51b5ec208ee9cb25b55d87282435f27b51b5ec208ee9cb25b55d8728243de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d".hexToBa(),
            key,
        )
    }
}