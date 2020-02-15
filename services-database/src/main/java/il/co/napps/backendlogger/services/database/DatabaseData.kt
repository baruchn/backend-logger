package il.co.napps.backendlogger.services.database


private const val TAG = "DatabaseData"

data class DatabaseData(val timeReceivedMilli: Long, val url: String, val data: String)