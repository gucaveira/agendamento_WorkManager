package dominando.android.agendamento

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val firstName = inputData.getString(PARAM_FIRST_NAME)
        val outputData = Data.Builder()
            .putString(PARAM_NAME, "$firstName Pereira")
            .putInt(PARAM_AGE, 24)
            .putLong(PARAM_TIME, System.currentTimeMillis())
            .build()

        return Result.success(outputData)
    }

    companion object {
        const val PARAM_FIRST_NAME = "first_name"
        const val PARAM_NAME = "name"
        const val PARAM_AGE = "age"
        const val PARAM_TIME = "time"
    }
}

class Worker1(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Thread.sleep(2000)
        val outputData = Data.Builder().putString("title", "Dominando Android").build()
        return Result.success(outputData)
    }
}

class Worker2(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Thread.sleep(1000)
        val outputData = Data.Builder().putString("subtitle", "com Kotlin").build()
        return Result.success(outputData)
    }
}

class Worker3(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val s = inputData.run { getString("title") + " " + getString("subtitle") }
        Log.d("NGVL", s)
        return Result.success()
    }
}

