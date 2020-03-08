package il.co.napps.backendlogger.tests

import il.co.napps.backendlogger.BackendLogger
import il.co.napps.backendlogger.services.os.log.Log
import org.junit.Test

import org.junit.Assert.*
import org.koin.dsl.koinApplication

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BasicTests {

    init {
        val log: Log
        BackendLogger.initialize("http://my.test.url/route/api") {
            sizeLimit = 5
        }
    }

    @Test
    fun testSendingLog() {
        assertEquals(4, 2 + 2)
    }
}
