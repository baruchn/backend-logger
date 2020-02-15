package il.co.napps.backendlogger.services.messages

import il.co.napps.backendlogger.services.database.DatabaseData
import il.co.napps.backendlogger.services.database.DatabaseService
import il.co.napps.backendlogger.services.rest.RestService
import il.co.napps.backendlogger.utils.DIProvidable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive


private const val TAG = "MessagesRepository"

interface MessagesRepository: DIProvidable {
    suspend fun sendMessage(message: Message)
}

@Suppress("unused")
internal class MessageRepositoryImpl(private val restService: RestService, private val databaseService: DatabaseService):
    MessagesRepository {

    override suspend fun sendMessage(message: Message) {
        val jsonElements = mutableMapOf<String, JsonElement>()
        for (entry in message.data.entries) {
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
//                    loge(TAG, "Data of type ${entry.value::class} is not supported")
                }
            }
        }
        val str = JsonObject(jsonElements)
        databaseService.insert(DatabaseData(message.timeReceivedMilli, message.url, str.toString()))
        databaseService.getOldest()?.run {
            restService.sendJsonStringMessage(url, data)
        }
    }
}