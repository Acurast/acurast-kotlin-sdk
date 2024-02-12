package acurast.rpc

import acurast.codec.extensions.fromSS58
import acurast.codec.extensions.hexToBa
import acurast.codec.type.AccountId32
import acurast.codec.type.acurast.JobIdentifier
import acurast.codec.type.acurast.MultiOrigin
import acurast.rpc.engine.RpcEngine
import acurast.rpc.engine.http.HttpRpcEngine
import acurast.rpc.pallet.orchestrator.JobAssignmentWithRegistration
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AcurastRpcIntegrationTest {
    private lateinit var rpcEngine: RpcEngine

    private lateinit var acurastRpc: AcurastRpc

    @Before
    fun setup() {
        // acurastRpc = AcurastRpc(HttpRpcEngine("ws://127.0.0.1:8803"));
        acurastRpc = AcurastRpc(HttpRpcEngine("ws://wss.collator-1.acurast.papers.tech"));
    }

    @Test
    fun `Get Job Matches from storage`() {
        // 5EWHjxPcybrxWKMhJc8XBKx9dkj1KhqSNXfFPRbksc3sRDtL
        val account = "6bfbaaa65ef911bcfc509b220d24e765ccef70b00fa3532b36eb2a12242a4718"

        val response = runBlocking {
            val assignedJobs = acurastRpc.getAssignedJobs(account.hexToBa())
            assignedJobs.map { assignment ->
                acurastRpc.getJobRegistration(assignment.jobId)?.let { job ->
                    JobAssignmentWithRegistration(job, assignment)
                }
            }
        }
        // response.prettyPrint()
    }

    @Test
    fun `Get Job Matches with Registration from orchestratorMatchedJobs()`() {
        // 5EWHjxPcybrxWKMhJc8XBKx9dkj1KhqSNXfFPRbksc3sRDtL
        val account = "6bfbaaa65ef911bcfc509b220d24e765ccef70b00fa3532b36eb2a12242a4718"

        val response = runBlocking {
            acurastRpc.chain.orchestratorMatchedJobs(AccountId32(account.hexToBa()))
        }
        // response.prettyPrint()
    }

    @Test
    fun `Compare Job Matches and registrations from storage with orchestratorMatchedJobs()`() {
        // 5EWHjxPcybrxWKMhJc8XBKx9dkj1KhqSNXfFPRbksc3sRDtL
        val account = "6bfbaaa65ef911bcfc509b220d24e765ccef70b00fa3532b36eb2a12242a4718"

        val jobsStorage = runBlocking {
            val assignedJobs = acurastRpc.getAssignedJobs(account.hexToBa())
            assignedJobs.map { assignment ->
                acurastRpc.getJobRegistration(assignment.jobId)?.let { job ->
                    JobAssignmentWithRegistration(job, assignment)
                }
            }.filterNotNull()
        }
        // jobsStorage.prettyPrint()

        val jobs = runBlocking {
            acurastRpc.chain.orchestratorMatchedJobs(AccountId32(account.hexToBa()))
        }
        // jobs.prettyPrint()

        assertEquals(jobsStorage.toSet(), jobs.toSet())
    }

    @Test
    fun `Get from orchestratorJobEnvironment()`() {
        val consumer = "5CcduNKcpGPF8mptXoq2yyUW5SQpX4bDiKxGwthGZpaowmvW"
        val source = "5EWHjxPcybrxWKMhJc8XBKx9dkj1KhqSNXfFPRbksc3sRDtL"

        val response = runBlocking {
            acurastRpc.chain.orchestratorJobEnvironment(
                JobIdentifier(
                    origin = MultiOrigin(MultiOrigin.Kind.Acurast, consumer.fromSS58()), 1015.toBigInteger()
                ), AccountId32(source.fromSS58())
            )
        }
        response?.prettyPrint()

        assertEquals("test", response?.vars?.get(0)?.key)
    }

    @Test
    fun `Get from orchestratorJobEnvironment(), empty`() {
        val consumer = "5CcduNKcpGPF8mptXoq2yyUW5SQpX4bDiKxGwthGZpaowmvW"
        val source = "5CcduNKcpGPF8mptXoq2yyUW5SQpX4bDiKxGwthGZpaowmvW"

        val response = runBlocking {
            acurastRpc.chain.orchestratorJobEnvironment(
                JobIdentifier(
                    origin = MultiOrigin(MultiOrigin.Kind.Acurast, consumer.fromSS58()), 1015.toBigInteger()
                ), AccountId32(source.fromSS58())
            )
        }
        response?.prettyPrint()

        assertNull(response)
    }

    @Test
    fun `Get from orchestratorIsAttested()`() {
        val source = "5EWHjxPcybrxWKMhJc8XBKx9dkj1KhqSNXfFPRbksc3sRDtL"

        val response = runBlocking {
            acurastRpc.chain.orchestratorIsAttested(
                AccountId32(source.fromSS58())
            )
        }

        assertEquals(response, true)
    }
}

fun Any.prettyPrint(): String {

    var indentLevel = 0
    val indentWidth = 4

    fun padding() = "".padStart(indentLevel * indentWidth)

    val toString = toString()

    val stringBuilder = StringBuilder(toString.length)

    var i = 0
    while (i < toString.length) {
        when (val char = toString[i]) {
            '(', '[', '{' -> {
                indentLevel++
                stringBuilder.appendLine(char).append(padding())
            }

            ')', ']', '}' -> {
                indentLevel--
                stringBuilder.appendLine().append(padding()).append(char)
            }

            ',' -> {
                stringBuilder.appendLine(char).append(padding())
                // ignore space after comma as we have added a newline
                val nextChar = toString.getOrElse(i + 1) { char }
                if (nextChar == ' ') i++
            }

            else -> {
                stringBuilder.append(char)
            }
        }
        i++
    }

    return stringBuilder.toString()
}