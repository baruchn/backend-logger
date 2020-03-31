package il.co.napps.backendlogger.services.messages

import il.co.napps.backendlogger.services.database.DatabaseData
import il.co.napps.backendlogger.services.database.DatabaseService
import il.co.napps.backendlogger.services.os.log.Log
import il.co.napps.backendlogger.services.rest.RestDataSerializer
import il.co.napps.backendlogger.services.rest.RestService
import il.co.napps.backendlogger.utils.DIProvidable
import java.io.*

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
internal class MessageRepositoryImpl(
    private val logger: Log,
    private val restService: RestService,
    private val databaseService: DatabaseService,
    private val serializer: RestDataSerializer
): MessagesRepository {

    @Synchronized
    override fun enqueueMessage(message: Message, retries: Int) {
        if (message.data.isEmpty()) {
            logger.w(TAG, "Not sending messages with empty data")
            return
        }
        databaseService.upsert(DatabaseData(message.timeReceivedMilli, message.url, serializeData(message.data), if (retries >=0 ) retries.toLong() else 0))
    }

    private fun serializeData(data: Map<String, Any>): ByteArray {
        try {
            ByteArrayOutputStream().use { byteStream ->
                ObjectOutputStream(byteStream).use { objStream ->
                    objStream.writeObject(data)
                    objStream.flush()
                    return byteStream.toByteArray()
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to serialize data to send: ${e.message}")
        }
    }

    private fun deserializeData(data: ByteArray): Map<String, Any> {
        try {
            ByteArrayInputStream(data).use { byteStream ->
                ObjectInputStream(byteStream).use { objStream ->
                    return objStream.readObject() as Map<String, Any>
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to de-serialize data to send: ${e.message}")
        }
    }

//    private fun messageDataToString(data: Map<String, Serializable>): String {
//        val jsonElements = mutableMapOf<String, JsonElement>()
//        for (entry in data.entries) {
//            val baos = ByteArrayOutputStream()
//            val oos = ObjectOutputStream(baos)
//            oos.writeObject(entry.value)
//            // TODO: 19/02/2020 support more types
//            when (entry.value) {
//                is String -> {
//                    jsonElements[entry.key] = JsonPrimitive(entry.value as String)
//                }
//                is Number -> {
//                    jsonElements[entry.key] = JsonPrimitive(entry.value as Number)
//                }
//                is Boolean -> {
//                    jsonElements[entry.key] = JsonPrimitive(entry.value as Boolean)
//                }
//                else -> {
//                    throw UnsupportedTypeException("Data of type ${entry.value::class} is not supported")
//                }
//            }
//        }
//        return JsonObject(jsonElements).toString()
//    }
//
//    private fun stringToMessageData(str: String): Map<String, Any> {
//        return json.parse(MapSerializer(String.serializer(), JsonPrimitive.serializer()), str)
//    }

    override suspend fun trySendingMessages(): Boolean {
        while (getMessagesCount() > 0) {
            databaseService.getOldest()?.run {
                when {
                    retries <= 0L -> {
                        databaseService.remove(timeReceivedMilli)
                    }
                    restService.sendMessage(url, deserializeData(message), serializer) -> {
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
}