package il.co.napps.backendlogger.services.database

data class DatabaseData(val timeReceivedMilli: Long, val url: String, val message: ByteArray, var retries: Long) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatabaseData

        if (timeReceivedMilli != other.timeReceivedMilli) return false
        if (url != other.url) return false
        if (!message.contentEquals(other.message)) return false
        if (retries != other.retries) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timeReceivedMilli.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + message.contentHashCode()
        result = 31 * result + retries.hashCode()
        return result
    }
}