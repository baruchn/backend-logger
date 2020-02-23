package il.co.napps.backendlogger.services.os.log

import il.co.napps.backendlogger.utils.DIProvidable

interface Log: DIProvidable {
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String)
    fun e(tag: String, message: String)
}