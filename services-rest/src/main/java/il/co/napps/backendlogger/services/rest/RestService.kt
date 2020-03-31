package il.co.napps.backendlogger.services.rest

import il.co.napps.backendlogger.services.os.log.Log
import il.co.napps.backendlogger.services.os.rest.RestProvider
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.url

private const val TAG = "RestService"

@Suppress
interface RestService {
    suspend fun sendMessage(url: String, message: Map<String, Any>, serializer: RestDataSerializer): Boolean
}

@Suppress("unused")
internal class RestServiceImpl(private val logger: Log, provider: RestProvider): RestService {

    private val client: HttpClient = provider.getClient()

    override suspend fun sendMessage(url: String, message: Map<String, Any>, serializer: RestDataSerializer): Boolean {
        logger.d(TAG, "sendMessage() called with: url = [$url], message = [$message], serializer = [$serializer]")

        return try {
            client.post<Any?> {
                url(url)
                body = serializer.serialize(message)
            }
            true
        } catch (cause: Throwable) {
            logger.e(TAG, "postString: ${cause.message}")
            false
        }
    }
}