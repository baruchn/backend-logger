package il.co.napps.backendlogger

import il.co.napps.backendlogger.services.messages.Message
import il.co.napps.backendlogger.services.messages.MessagesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


private const val TAG = "BackendLogger"

@Suppress("unused")
class BackendLogger(private val url: String) {

    init {
        initializeDi()
    }

    fun sendMessage(messageData: Map<String, Any>) {
        GlobalScope.launch(Dispatchers.IO) {
            get<MessagesRepository>().sendMessage(Message(System.currentTimeMillis(), url, messageData))
        }
    }
}