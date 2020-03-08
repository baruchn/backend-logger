package il.co.napps.backendlogger

import android.support.annotation.Keep
import il.co.napps.backendlogger.services.messages.Message
import il.co.napps.backendlogger.services.messages.MessagesRepository
import il.co.napps.backendlogger.services.os.scheduler.Scheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


private const val TAG = "BackendLogger"

@Suppress("unused", "MemberVisibilityCanBePrivate")
@Keep
class BackendLogger(val url: String) {

    companion object {
        private val globalOptions = GlobalOptions()
        private val messagesRepository: Lazy<MessagesRepository> = inject()
        private val scheduler: Lazy<Scheduler> = inject()
        private val acceptedClasses = listOf<Class<Any>>(String.javaClass, Int.javaClass, Long.javaClass, Float.javaClass, Double.javaClass, Boolean.javaClass)

        fun configureGlobal(block: GlobalOptions.() -> Unit = {}) {
            this.globalOptions.apply(block)
        }
    }

    private val localOptions = LocalOptions()
    private val options = OptionsResolver(localOptions, globalOptions)

    constructor(url: String, block: LocalOptions.() -> Unit = {}) : this(url) {
        configure(block)
    }

    fun configure(block: LocalOptions.() -> Unit = {}) {
        this.localOptions.apply(block)
    }

    @Synchronized
    fun sendMessage(messageData: Map<String, Any>) {

        if (messagesRepository.value.getMessagesCount(url) == options.sizeLimit || messagesRepository.value.getMessagesCount() == globalOptions.sizeLimit) {
            messagesRepository.value.removeOldest()
        }

        messagesRepository.value.enqueueMessage(
            Message(
                System.currentTimeMillis(),
                url,
                messageData
            )
        )

        GlobalScope.launch(Dispatchers.IO) {
            if (!messagesRepository.value.trySendingMessages()) {
                // TODO: 08/03/2020 limit retry attempts
                scheduler.value.schedule(ScheduledWork::class.java)
            }
        }
    }

    fun isSupportedDataType(clazz: Class<Any>): Boolean {
        return acceptedClasses.contains(clazz)
    }

    @Keep
    class GlobalOptions internal constructor() {
        var sizeLimit = 100
    }

    @Keep
    class LocalOptions internal constructor() {
        var sizeLimit: Int? = null
    }

    private class OptionsResolver(private val localOptions: LocalOptions, private val globalOptions: GlobalOptions) {
        val sizeLimit: Int
            get() { return localOptions.sizeLimit ?: globalOptions.sizeLimit }
    }
}