package acurast.rpc

import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.cert.Certificate
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

public class Networking {
    public companion object {
        private const val HTTP_CONNECT_TIMEOUT = 30000;

        private val executor: ExecutorService = Executors.newFixedThreadPool(10)

        public fun httpsRequest(
            url: URL,
            method: String,
            body: ByteArray = ByteArray(0),
            headers: Map<String, String>,
            successCallback: (String, ByteArray) -> Unit,
            errorCallback: (Exception) -> Unit,
            connectTimeout: Int = HTTP_CONNECT_TIMEOUT,
            readTimeout: Int = HTTP_CONNECT_TIMEOUT
        ) {
            executor.submit {
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                try {
                    urlConnection.requestMethod = method
                    urlConnection.connectTimeout = connectTimeout
                    urlConnection.readTimeout = readTimeout

                    for (header in headers.entries) {
                        urlConnection.setRequestProperty(header.key, header.value)
                    }

                    if (body.isNotEmpty()) {
                        urlConnection.doOutput = true
                        urlConnection.outputStream.write(
                            body,
                            0,
                            body.size
                        )
                        urlConnection.outputStream.close()
                    }

                    val payload = urlConnection.inputStream.bufferedReader().readText()
                    urlConnection.inputStream.close()
                    //val certificateSha256 = getCertificatePin(urlConnection.serverCertificates.first())

                    successCallback(payload, byteArrayOf())
                } catch (exception: Exception) {
                    errorCallback(exception)
                } finally {
                    urlConnection.disconnect()
                }
            }
        }

        public fun httpsPostString(
            url: URL,
            string: String,
            headers: Map<String, String>,
            success: (String, ByteArray) -> Unit,
            error: (Exception) -> Unit,
            connectTimeout: Int = HTTP_CONNECT_TIMEOUT,
            readTimeout: Int = HTTP_CONNECT_TIMEOUT
        ) {
            httpsRequest(url, "POST", string.toByteArray(), headers, success, error, connectTimeout, readTimeout)
        }

        public fun httpsGetString(
            url: URL,
            headers: Map<String, String>,
            success: (String, ByteArray) -> Unit,
            error: (Exception) -> Unit,
            connectTimeout: Int = HTTP_CONNECT_TIMEOUT,
            readTimeout: Int = HTTP_CONNECT_TIMEOUT
        ) {
            httpsRequest(url, "GET", ByteArray(0), headers, success, error, connectTimeout, readTimeout)
        }

        private fun getCertificatePin(certificate: Certificate): ByteArray {
            val md = MessageDigest.getInstance("SHA-256")
            return md.digest(certificate.publicKey.encoded)
        }
    }
}
