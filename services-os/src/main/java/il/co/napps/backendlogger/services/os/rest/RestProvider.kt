package il.co.napps.backendlogger.services.os.rest

import io.ktor.client.HttpClient


@Suppress
interface RestProvider {
    fun getClient(): HttpClient
}