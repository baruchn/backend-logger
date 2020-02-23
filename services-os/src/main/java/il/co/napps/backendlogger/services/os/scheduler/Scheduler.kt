package il.co.napps.backendlogger.services.os.scheduler

import il.co.napps.backendlogger.utils.DIProvidable

interface Scheduler: DIProvidable {
    fun <T: Work> schedule(clazz: Class<T>)
}