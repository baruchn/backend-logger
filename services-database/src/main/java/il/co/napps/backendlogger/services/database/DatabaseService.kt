package il.co.napps.backendlogger.services.database

import il.co.napps.backendlogger.services.os.database.DatabaseDriverProvider
import il.co.napps.backendlogger.services.os.log.Log

interface DatabaseService {
    fun upsert(data: DatabaseData)
    fun getOldest(): DatabaseData?
    fun remove(time: Long)
    fun count(): Int
    fun countForUrl(url: String): Int
    fun removeAll()
    fun removeOldest()
}

@Suppress("unused")
internal class DatabaseServiceImpl(private val logger: Log, driverProvider: DatabaseDriverProvider): DatabaseService {

    private val database = BackendLoggerDatabase(driverProvider.driver)

    override fun upsert(data: DatabaseData) {
        database.backendLoggerQueries.upsert(data.timeReceivedMilli, data.url, data.message, data.retries)
    }

    override fun getOldest(): DatabaseData? {
        val data = database.backendLoggerQueries.getOldest().executeAsOneOrNull()
        data?.run {
            return DatabaseData(time, url, message, retries)
        }
        return null
    }

    override fun remove(time: Long) {
        database.backendLoggerQueries.remove(time)
    }

    override fun count(): Int {
        return database.backendLoggerQueries.count().executeAsOne().toInt()
    }

    override fun countForUrl(url: String): Int {
        return database.backendLoggerQueries.countForUrl(url).executeAsOne().toInt()
    }

    override fun removeAll() {
        database.backendLoggerQueries.removeAll()
    }

    override fun removeOldest() {
        database.backendLoggerQueries.removeOldest()
    }
}