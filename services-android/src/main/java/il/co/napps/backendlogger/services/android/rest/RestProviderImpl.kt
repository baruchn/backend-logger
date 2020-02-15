@file:Suppress("unused")

package il.co.napps.backendlogger.services.android.rest

import il.co.napps.backendlogger.BuildConfig
import il.co.napps.backendlogger.services.android.log.LogImpl
import il.co.napps.backendlogger.services.os.RestProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging

private const val TAG = "RestProvider"

internal class RestProviderImpl: RestProvider {
    override fun getClient(): HttpClient {
        return HttpClient(Android) {
            install(JsonFeature)
            install(Logging) {
                logger = LogImpl()
                level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
            }
        }
    }
}