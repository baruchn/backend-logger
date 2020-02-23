package il.co.napps.backendlogger

import il.co.napps.backendlogger.services.os.log.Log
import il.co.napps.backendlogger.services.os.scheduler.Work


private const val TAG = "ScheduledWork"

class ScheduledWork: Work {
    override fun doWork(): Boolean {
        val log: Log = get()
        log.d(TAG, "executing scheduled task!!!")
        return true
    }
}