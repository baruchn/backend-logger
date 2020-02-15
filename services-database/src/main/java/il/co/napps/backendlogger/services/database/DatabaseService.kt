package il.co.napps.backendlogger.services.database

import il.co.napps.backendlogger.services.os.Log
import java.util.*

interface DatabaseService {
    fun insert(data: DatabaseData)
    fun getOldest(): DatabaseData?
}

@Suppress("unused")
internal class DatabaseServiceImpl(private val logger: Log): DatabaseService {

    private val queue: Queue<DatabaseData> = LinkedList()

    override fun insert(data: DatabaseData) {
        queue.offer(data)
    }

    override fun getOldest(): DatabaseData? {
        return queue.poll()
    }

}