package il.co.napps.backendlogger.services.android.log

import il.co.napps.backendlogger.services.os.Log


@Suppress("unused")
internal class LogImpl: Log {
    override fun d(tag: String, message: String) {
        android.util.Log.d(tag, message)
    }

    override fun i(tag: String, message: String) {
        android.util.Log.i(tag, message)
    }

    override fun w(tag: String, message: String) {
        android.util.Log.w(tag, message)
    }

    override fun e(tag: String, message: String) {
        android.util.Log.e(tag, message)
    }

}