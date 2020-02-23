package il.co.napps.backendlogger.services.messages

import il.co.napps.backendlogger.services.database.DatabaseData
import il.co.napps.backendlogger.services.database.DatabaseService
import il.co.napps.backendlogger.services.os.log.Log
import il.co.napps.backendlogger.services.rest.RestService
import il.co.napps.backendlogger.utils.DIProvidable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private const val TAG = "MessagesRepository"

interface MessagesRepository: DIProvidable {
    fun enqueueMessage(message: Message)
    suspend fun trySendingMessages(): Boolean
    fun getMessagesCount(): Int
    fun removeAll()
    fun removeOldest()
}

@Suppress("unused")
internal class MessageRepositoryImpl(private val logger: Log, private val restService: RestService, private val databaseService: DatabaseService):
    MessagesRepository {

    @Synchronized
    override fun enqueueMessage(message: Message) {
        val jsonElements = mutableMapOf<String, JsonElement>()
        for (entry in message.data.entries) {
            // TODO: 19/02/2020 support more types
            when (entry.value) {
                is String -> {
                    jsonElements[entry.key] = JsonPrimitive(entry.value as String)
                }
                is Number -> {
                    jsonElements[entry.key] = JsonPrimitive(entry.value as Number)
                }
                is Boolean -> {
                    jsonElements[entry.key] = JsonPrimitive(entry.value as Boolean)
                }
                else -> {
                    throw UnsupportedTypeException("Data of type ${entry.value::class} is not supported")
                }
            }
        }
        val str = JsonObject(jsonElements)
        databaseService.insert(DatabaseData(message.timeReceivedMilli, message.url, str.toString()))
    }

    override suspend fun trySendingMessages(): Boolean {
        var messageTime = sendOldestMessage()
        while (messageTime != null) {
            databaseService.remove(messageTime)
            messageTime = sendOldestMessage()
        }
        return getMessagesCount() == 0
    }

    private suspend fun sendOldestMessage(): Long? {
        databaseService.getOldest()?.run {
            if (restService.sendJsonStringMessage(url, message)) {
                return timeReceivedMilli
            }
        }
        return null
    }

    override fun getMessagesCount(): Int {
        return databaseService.count()
    }

    override fun removeAll() {
        databaseService.removeAll()
    }

    override fun removeOldest() {
        databaseService.removeOldest()
    }
}