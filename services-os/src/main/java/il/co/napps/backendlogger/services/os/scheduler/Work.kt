package il.co.napps.backendlogger.services.os.scheduler

interface Work {
    fun doWork(): Boolean
}