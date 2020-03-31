package il.co.napps.backendlogger.services.serializer.json

import il.co.napps.backendlogger.services.os.log.Log
import il.co.napps.backendlogger.services.os.rest.RestProvider
import il.co.napps.backendlogger.services.rest.RestDataSerializer
import il.co.napps.backendlogger.utils.DIProvidable
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import kotlinx.serialization.json.*


private const val TAG = "JsonRestDataSerializer"

@Suppress("unused")
class JsonRestDataSerializer(private val logger: Log, restProvider: RestProvider): RestDataSerializer, DIProvidable {

    init {
        restProvider.getClient().config {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

    override fun serialize(data: Map<String, Any>): OutgoingContent {
        val jsonElements = mutableMapOf<String, JsonElement>()
        for (entry in data.entries) {
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
                    logger.e(TAG, "Data of type ${entry.value::class} is not supported")
                }
            }
        }
        return TextContent(JsonObject(jsonElements).toString(), ContentType.Application.Json)
    }
}