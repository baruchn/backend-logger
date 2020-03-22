package il.co.napps.backendlogger.services.database

data class DatabaseData(val timeReceivedMilli: Long, val url: String, val message: String, var retries: Long)