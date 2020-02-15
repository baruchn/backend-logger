package il.co.napps.backendlogger.services.os

import io.ktor.client.HttpClient


@Suppress
interface RestProvider {
    fun getClient(): HttpClient
}