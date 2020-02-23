package il.co.napps.backendlogger

import il.co.napps.backendlogger.services.messages.Message
import il.co.napps.backendlogger.services.messages.MessagesRepository
import il.co.napps.backendlogger.services.os.scheduler.Scheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


private const val TAG = "BackendLogger"

@Suppress("unused")
object BackendLogger {

    private val messagesRepository: Lazy<MessagesRepository> = inject()
    private val scheduler: Lazy<Scheduler> = inject()
    private val options = Options()
    private lateinit var url: String
    private val acceptedClasses = listOf<Class<Any>>(String.javaClass, Int.javaClass, Long.javaClass, Float.javaClass, Double.javaClass, Boolean.javaClass)

    fun initialize(url: String, configure: Options.() -> Unit = {}) {
        this.url = url
        this.options.apply(configure)
    }

    fun sendMessage(messageData: Map<String, Any>) {
        if (::url.isInitialized) {

            if (messagesRepository.value.getMessagesCount() == options.sizeLimit) {
                messagesRepository.value.removeOldest()
            }

            GlobalScope.launch(Dispatchers.IO) {
                messagesRepository.value.enqueueMessage(
                    Message(
                        System.currentTimeMillis(),
                        url,
                        messageData
                    )
                )
                if (!messagesRepository.value.trySendingMessages()) {
                    // TODO: 19/02/2020 reschedule
                }

                scheduler.value.schedule(ScheduledWork::class.java)
            }
        }

        // TODO: 19/02/2020 log uninitialized?
    }

    fun isSupportedType(clazz: Class<Any>): Boolean {
        return acceptedClasses.contains(clazz)
    }

    class Options internal constructor() {
        var sizeLimit = 100
    }
}