package il.co.napps.backendlogger.services.messages.tests

import il.co.napps.backendlogger.services.database.DatabaseData
import il.co.napps.backendlogger.services.database.DatabaseService
import il.co.napps.backendlogger.services.messages.Message
import il.co.napps.backendlogger.services.messages.MessageRepositoryImpl
import il.co.napps.backendlogger.services.os.log.Log
import il.co.napps.backendlogger.services.rest.RestService
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@UnstableDefault
@ImplicitReflectionSerializer
class BasicTests {

    private val log: Log
        get() {
            val log = mockk<Log>(relaxed = true)
            every { log.e(any(), any()) } answers {
                println("$${arg<String>(0)}: ${arg<String>(1)}")
                nothing
            }
            return log
        }

    private val restService = mockk<RestService>()
    private val databaseService = mockk<DatabaseService>(relaxed = true)
    private val messagesRepository = MessageRepositoryImpl(log, restService, databaseService)

    @Test
    fun testEnqueueMessage() {

        val sendUrl = "https://www.my.domain/api/route"

        val messageData = mutableMapOf<String, Any>()
        val strKey = "Str"
        val strValue = "Test message"
        val intKey = "Int"
        val intValue = Integer.MIN_VALUE
        val longKey = "Long"
        val longValue = Long.MAX_VALUE
        val floatKey = "Float"
        val floatValue = Float.MIN_VALUE
        val doubleKey = "Double"
        val doubleValue = Double.MAX_VALUE
        messageData[strKey] = strValue
        messageData[intKey] = intValue
        messageData[longKey] = longValue
        messageData[floatKey] = floatValue
        messageData[doubleKey] = doubleValue

        val retries = 3
        val timeReceivedMilli = System.currentTimeMillis()
        val message = Message(timeReceivedMilli, sendUrl, messageData)

        val jsonElements = mutableMapOf<String, JsonElement>()
        jsonElements[strKey] = JsonPrimitive(strValue)
        jsonElements[intKey] = JsonPrimitive(intValue)
        jsonElements[longKey] = JsonPrimitive(longValue)
        jsonElements[floatKey] = JsonPrimitive(floatValue)
        jsonElements[doubleKey] = JsonPrimitive(doubleValue)
        val jsonObject = JsonObject(jsonElements)

        every { databaseService.upsert(any()) } answers {
            val data = arg<DatabaseData>(0)
            assert(data.timeReceivedMilli == timeReceivedMilli) { "Invalid time received" }
            assert(data.url == sendUrl) { "Invalid url" }
            assert(data.retries == retries.toLong()) { "invalid retries" }
            assert(data.message == jsonObject.toString()) { "Invalid message. expected \"$jsonObject\" but received \"${data.message}\"" }
            nothing
        }

        messagesRepository.enqueueMessage(message, retries)

        verify(exactly = 1) { databaseService.upsert(any()) }
    }

    @Test
    fun testSendingMessages() {
        var count = 10
        val dbData = DatabaseData(0, "", "", 1)

        every { databaseService.count() } answers { --count }

        every { databaseService.getOldest() } answers { dbData }

        coEvery { restService.sendJsonStringMessage(any(), any()) } answers { true }

        runBlocking { messagesRepository.trySendingMessages() }

        verify(exactly = count) { databaseService.remove(any()) }
        coVerify(exactly = count) { restService.sendJsonStringMessage(any(), any()) }
    }

    @Test
    fun testRetriesFail() {
        val count = 10
        var dbData: DatabaseData? = DatabaseData(0, "", "", count.toLong())

        every { databaseService.count() } answers { if (dbData != null) 1 else 0 }

        every { databaseService.getOldest() } answers { dbData }

        every { databaseService.upsert(any()) } answers {
            dbData = arg(0)
        }

        every { databaseService.remove(0) } answers {
            dbData = null
        }

        coEvery { restService.sendJsonStringMessage(any(), any()) } answers { false }

        runBlocking { messagesRepository.trySendingMessages() }

        verify(exactly = count - 1) { databaseService.upsert(any()) }
        verify(exactly = 1) { databaseService.remove(any()) }
        coVerify(exactly = count) { restService.sendJsonStringMessage(any(), any()) }

    }

    @Test
    fun testRetriesSuccess() {
        val fails = 5
        var failsCount = fails
        var dbData: DatabaseData? = DatabaseData(0, "", "", (failsCount + 1).toLong())

        every { databaseService.count() } answers { if (dbData != null) 1 else 0 }

        every { databaseService.getOldest() } answers { dbData }

        every { databaseService.upsert(any()) } answers {
            dbData = arg(0)
        }

        every { databaseService.remove(0) } answers {
            dbData = null
        }

        coEvery { restService.sendJsonStringMessage(any(), any()) } answers {
            if (failsCount > 0) {
                failsCount--
                false
            } else {
                true
            }
        }

        runBlocking { messagesRepository.trySendingMessages() }

        verify(exactly = fails) { databaseService.upsert(any()) }
        verify(exactly = 1) { databaseService.remove(any()) }
        coVerify(exactly = fails + 1) { restService.sendJsonStringMessage(any(), any()) }

    }

}
