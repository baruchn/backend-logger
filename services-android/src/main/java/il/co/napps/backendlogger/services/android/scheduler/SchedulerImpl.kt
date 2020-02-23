package il.co.napps.backendlogger.services.android.scheduler

import android.content.Context
import androidx.work.*
import il.co.napps.backendlogger.services.android.context
import il.co.napps.backendlogger.services.os.scheduler.Scheduler
import il.co.napps.backendlogger.services.os.scheduler.Work


private const val TAG = "BackendLoggerWorker"

private const val WORKER_CLASS_NAME_KEY = "workerClassName"

@Suppress("unused")
internal class SchedulerImpl: Scheduler {
    override fun <T : Work> schedule(clazz: Class<T>) {
        val uploadWorkRequest = OneTimeWorkRequestBuilder<WorkWrapper>()
            .setInputData(Data.Builder().putString(WORKER_CLASS_NAME_KEY, clazz.canonicalName).build())
            .build()
        WorkManager.getInstance(context).enqueue(uploadWorkRequest)
    }
}

internal class WorkWrapper(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {

        val result = (Class.forName(inputData.getString(WORKER_CLASS_NAME_KEY)!!, false, applicationContext.classLoader).constructors.first().newInstance() as Work).doWork()

        return if (result) Result.success() else Result.failure()
    }
}