package il.co.napps.backendlogger.services.rest.tests

import il.co.napps.backendlogger.services.os.log.Log
import il.co.napps.backendlogger.services.os.rest.RestProvider
import il.co.napps.backendlogger.services.rest.RestServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JsonFeature
import io.ktor.http.ContentType
import io.ktor.http.fullPath
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import org.junit.Test
import kotlin.test.fail


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@UnstableDefault
@ImplicitReflectionSerializer
class BasicTests {

    @Test
    fun testSendingMessage() {

        val sendUrl = "https://www.my.domain/api/route"

        val log = object: Log {
            override fun d(tag: String, message: String) {
                println("D:$tag: $message")
            }

            override fun i(tag: String, message: String) {
                println("I:$tag: $message")
            }

            override fun w(tag: String, message: String) {
                println("W:$tag: $message")
            }

            override fun e(tag: String, message: String) {
                println("E:$tag: $message")
            }
        }

        val restProvider = mockk<RestProvider>()
        val httpClient = HttpClient(MockEngine) {
            install(JsonFeature)
            engine {
                addHandler { request ->
                    when(request.url.toString()) {
                        sendUrl -> {
                            assert(request.headers.contains("Accept", ContentType.Application.Json.toString())) { "Missing JSON Header" }
                            respond("")
                        }
                        else -> fail("Unknown url: ${request.url.fullPath}")
                    }
                }
            }
        }

        every { restProvider.getClient() } returns httpClient

        val restService = RestServiceImpl(log, restProvider)

        runBlocking { assert(restService.sendJsonStringMessage(sendUrl, "{\"message\":\"Test Message\"}")) { "Failed to send message. See logs." } }
    }
}
