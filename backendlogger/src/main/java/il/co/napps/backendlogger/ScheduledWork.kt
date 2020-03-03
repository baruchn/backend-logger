package il.co.napps.backendlogger

import il.co.napps.backendlogger.services.messages.MessagesRepository
import il.co.napps.backendlogger.services.os.log.Log
import il.co.napps.backendlogger.services.os.scheduler.Work
import kotlinx.coroutines.runBlocking


private const val TAG = "ScheduledWork"

internal class ScheduledWork: Work {

    private val messagesRepository: Lazy<MessagesRepository> = inject()

    override fun doWork(): Boolean {
        val log: Log = get()
        log.d(TAG, "doWork() called")
        runBlocking { messagesRepository.value.trySendingMessages() }
        return messagesRepository.value.getMessagesCount() == 0
    }
}