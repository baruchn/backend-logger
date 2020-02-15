package il.co.napps.backendlogger.services.messages

private const val TAG = "Message"

data class Message(val timeReceivedMilli: Long, val url: String, val data: Map<String, Any>)