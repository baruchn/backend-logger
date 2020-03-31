package il.co.napps.backendlogger.services.messages

import il.co.napps.backendlogger.services.database.DatabaseData
import il.co.napps.backendlogger.services.database.DatabaseService
import il.co.napps.backendlogger.services.os.log.Log
import il.co.napps.backendlogger.services.rest.RestService
import il.co.napps.backendlogger.utils.DIProvidable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.text.DecimalFormat

private const val TAG = "MessagesRepository"

interface MessagesRepository: DIProvidable {
    fun enqueueMessage(message: Message, retries: Int = 0)
    suspend fun trySendingMessages(): Boolean
    fun getMessagesCount(): Int
    fun getMessagesCount(url: String): Int
    fun removeAll()
    fun removeOldest()
}

@Suppress("unused")
internal class MessageRepositoryImpl(private val logger: Log, private val restService: RestService, private val databaseService: DatabaseService):
    MessagesRepository {

    @Synchronized
    override fun enqueueMessage(message: Message, retries: Int) {
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
        if (jsonElements.isEmpty()) {
            logger.w(TAG, "Not sending messages with empty data")
            return
        }
        val str = JsonObject(jsonElements)
        databaseService.upsert(DatabaseData(message.timeReceivedMilli, message.url, str.toString(), if (retries >=0 ) retries.toLong() else 0))
    }

    override suspend fun trySendingMessages(): Boolean {
        while (getMessagesCount() > 0) {
            databaseService.getOldest()?.run {
                when {
                    retries <= 0L -> {
                        databaseService.remove(timeReceivedMilli)
                    }
                    restService.sendJsonStringMessage(url, message) -> {
                        databaseService.remove(timeReceivedMilli)
                    }
                    else -> {
                        retries--
                        if (retries <= 0L) {
                            databaseService.remove(timeReceivedMilli)
                        } else {
                            databaseService.upsert(this)
                        }
                    }
                }
            }
        }
        return getMessagesCount() == 0
    }

    override fun getMessagesCount(): Int {
        return databaseService.count()
    }

    override fun getMessagesCount(url: String): Int {
        return databaseService.countForUrl(url)
    }

    override fun removeAll() {
        databaseService.removeAll()
    }

    override fun removeOldest() {
        databaseService.removeOldest()
    }

    companion object {

    }
}