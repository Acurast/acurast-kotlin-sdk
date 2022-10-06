package acurast.rpc

import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

public class Networking {
    public companion object {
        private const val HTTP_CONNECT_TIMEOUT = 30000;

        private val executor: ExecutorService = Executors.newFixedThreadPool(10)

        private fun httpsRequest(
            url: URL,
            method: String,
            body: ByteArray = ByteArray(0),
            headers: Map<String, String>,
            successCallback: (String) -> Unit,
            errorCallback: (Exception) -> Unit,
            connectTimeout: Int = HTTP_CONNECT_TIMEOUT,
            readTimeout: Int = HTTP_CONNECT_TIMEOUT
        ) {
            executor.submit {
                var urlConnection: HttpURLConnection? = null;
                try {
                    urlConnection = url.openConnection() as HttpURLConnection
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

                    successCallback(payload)
                } catch (exception: Exception) {
                    errorCallback(exception)
                }

                urlConnection?.disconnect()
            }
        }

        public fun httpsPostString(
            url: URL,
            string: String,
            headers: Map<String, String>,
            success: (String) -> Unit,
            error: (Exception) -> Unit,
            connectTimeout: Int = HTTP_CONNECT_TIMEOUT,
            readTimeout: Int = HTTP_CONNECT_TIMEOUT
        ) {
            httpsRequest(url, "POST", string.toByteArray(), headers, success, error, connectTimeout, readTimeout)
        }

        public fun httpsGetString(
            url: URL,
            headers: Map<String, String>,
            success: (String) -> Unit,
            error: (Exception) -> Unit,
            connectTimeout: Int = HTTP_CONNECT_TIMEOUT,
            readTimeout: Int = HTTP_CONNECT_TIMEOUT
        ) {
            httpsRequest(url, "GET", ByteArray(0), headers, success, error, connectTimeout, readTimeout)
        }
    }
}
