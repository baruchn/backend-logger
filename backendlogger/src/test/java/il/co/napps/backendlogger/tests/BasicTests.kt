package il.co.napps.backendlogger.tests

import il.co.napps.backendlogger.*
import il.co.napps.backendlogger.di.DI
import il.co.napps.backendlogger.services.messages.Message
import il.co.napps.backendlogger.services.messages.MessagesRepository
import il.co.napps.backendlogger.services.os.log.Log
import il.co.napps.backendlogger.services.os.scheduler.Scheduler
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BasicTests {

    @Test
    fun testSendingMessage() {

        val log = mockk<Log>(relaxed = true)
        every { log.e(any(), any()) } answers {
            println("$${arg<String>(0)}: ${arg<String>(1)}")
            nothing
        }

        val messagesRepository = mockk<MessagesRepository>()
        val scheduler = mockk<Scheduler>()

        mockkStatic("il.co.napps.backendlogger.DIKt")

        val lazyMessagesRepository = mockk<Lazy<MessagesRepository>>()
        val lazyScheduler = mockk<Lazy<Scheduler>>()
        val diMock = mockk<DI>()

        every { initializeDi() } answers { nothing }
        every { di } returns diMock
        every { inject<MessagesRepository>() } returns lazyMessagesRepository
        every { inject<Scheduler>() } returns lazyScheduler
        every { lazyMessagesRepository.value } returns messagesRepository
        every { lazyScheduler.value } returns scheduler

        val url = "testUrl"
        val retriesCount = 1
        val messageDataMap = mutableMapOf<String, Any>()
        messageDataMap["message"] = "Test message"

        every { messagesRepository.getMessagesCount(url) } answers {
            assert(arg<String>(0) == url)
            1
        }
        every { messagesRepository.getMessagesCount() } returns 1
        every { messagesRepository.enqueueMessage(any(), any()) } answers {
            val message = arg<Message>(0)
            assert(message.data == messageDataMap)
            assert(message.url == url)
            assert(arg<Int>(1) == retriesCount)
            nothing
        }
        every { runBlocking { messagesRepository.trySendingMessages() } } answers { false }
        every { scheduler.schedule(ScheduledWork::class.java) } answers { nothing }

        val backendLogger = BackendLogger(url) {
            sizeLimit = 2
            retries = retriesCount
        }
        backendLogger.sendMessage(messageDataMap)

        verify(exactly = 1) { messagesRepository.enqueueMessage(any(), any()) }
        verify(exactly = 1) { runBlocking { messagesRepository.trySendingMessages() } }
        verify(exactly = 1) { scheduler.schedule(ScheduledWork::class.java) }        
    }

}
