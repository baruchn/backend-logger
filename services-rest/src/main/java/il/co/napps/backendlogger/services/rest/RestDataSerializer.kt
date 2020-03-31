package il.co.napps.backendlogger.services.rest

import io.ktor.http.content.OutgoingContent

interface RestDataSerializer {
    fun serialize(data: Map<String, Any>): OutgoingContent
}