package il.co.napps.backendlogger.services.messages

data class Message(val timeReceivedMilli: Long, val url: String, val data: Map<String, Any>)