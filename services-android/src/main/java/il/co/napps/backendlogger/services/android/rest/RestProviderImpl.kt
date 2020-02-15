@file:Suppress("unused")

package il.co.napps.backendlogger.services.android.rest

import il.co.napps.backendlogger.services.os.RestProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature

private const val TAG = "RestProvider"

internal class RestProviderImpl: RestProvider {
    override fun getClient(): HttpClient {
        return HttpClient(Android) {
            install(JsonFeature)
//            if (enableLogging) {
//            install(Logging) {
//                logger = LogWrapperImpl()
//                level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
//            }
//            }
        }
    }
}