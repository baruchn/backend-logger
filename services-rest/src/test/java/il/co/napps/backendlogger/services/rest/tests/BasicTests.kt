package il.co.napps.backendlogger.services.rest.tests

import il.co.napps.backendlogger.services.os.log.Log
import il.co.napps.backendlogger.services.os.rest.RestProvider
import il.co.napps.backendlogger.services.rest.RestServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JsonFeature
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.fullPath
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.junit.Test
import java.math.RoundingMode
import java.text.DecimalFormat
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
        val requestKey = "message"
        val requestValue = "Test Message"

        val log = mockk<Log>(relaxed = true)
        every { log.e(any(), any()) } answers {
            println("$${arg<String>(0)}: ${arg<String>(1)}")
            nothing
        }

        val restProvider = mockk<RestProvider>()
        var called = 0
        val httpClient = HttpClient(MockEngine) {
            install(JsonFeature)
            engine {
                addHandler { request ->
                    called++
                    when(request.url.toString()) {
                        sendUrl -> {
                            assert(request.headers.contains("Accept", ContentType.Application.Json.toString())) { "Missing JSON Header" }
                            try {
                                assert(Json.parseJson((request.body as TextContent).text).jsonObject[requestKey]!!.jsonObject["content"].toString().removePrefix("\"").removeSuffix("\"") == requestValue) { "Invalid message structure" }
                            } catch (e: Exception) {
                                fail("Failed to parse request body: ${e.message}")
                            }
                            respond("")
                        }
                        else -> fail("Unknown url: ${request.url.fullPath}")
                    }
                }
            }
        }

        every { restProvider.getClient() } returns httpClient

        val restService = RestServiceImpl(log, restProvider)

        val message = "{\"$requestKey\":\"$requestValue\"}"
        runBlocking { assert(restService.sendJsonStringMessage(sendUrl, message)) { "Failed to send message. See logs." } }

        assert(called == 1) { "Post called $called times" }

        verify(exactly = 1) { restProvider.getClient() }
    }


}
