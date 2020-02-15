package il.co.napps.backendlogger.services.rest

import il.co.napps.backendlogger.services.os.RestProvider
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@Suppress
interface RestService {
    suspend fun sendJsonStringMessage(url: String, message: String): Boolean
}

@Suppress("unused")
@UnstableDefault
@ImplicitReflectionSerializer
internal class RestServiceImpl(provider: RestProvider): RestService {

    val client: HttpClient = provider.getClient()

    override suspend fun sendJsonStringMessage(url: String, message: String): Boolean {
//        logd(TAG, "sendJsonStringMessage() called with: url = [$url], message = [$message]")
        val json = Json(JsonConfiguration.Stable)

        return try {
            client.post<Any?> {
                url(url)
                contentType(ContentType.Application.Json)
                body = json.parseJson(message)
            }
            true
        } catch (cause: Throwable) {
//            loge(TAG, "postString: ${cause.message}")
            false
        }
    }
}