package dominando.android.agendamento

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dominando.android.agendamento.databinding.ActivityMainBinding
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val workManager = WorkManager.getInstance(this)
    private var workId: UUID? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOneTime.setOnClickListener {
            val input = Data.Builder().putString(MyWork.PARAM_FIRST_NAME, "Gustavo").build()
            val request = OneTimeWorkRequest.Builder(MyWork::class.java).setInputData(input).build()
            observeAndEnqueue(request)
        }

        binding.btnPeriodic.setOnClickListener {
            val input = Data.Builder().putString(MyWork.PARAM_FIRST_NAME, "Gustavo").build()
            val request = PeriodicWorkRequest.Builder(
                MyWork::class.java,
                repeatInterval = 15,
                TimeUnit.MINUTES
            ).setInputData(input).build()
            observeAndEnqueue(request)
        }

        binding.btnStop.setOnClickListener {
            workId?.let { uuid ->
                workManager.cancelWorkById(uuid)
                //wm.cancelAllWork()
            }
        }

    }

    fun exemploDeUsoDoContConstraints() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiresStorageNotLow(false)
            .build()

        val request = OneTimeWorkRequest.Builder(MyWork::class.java)
            .setConstraints(constraints).build()
    }

    fun exemploDeChamaraEmCadeiaWorkManager() {
        val request1 = OneTimeWorkRequest.Builder(Worker1::class.java).build()
        val request2 = OneTimeWorkRequest.Builder(Worker2::class.java).build()
        val request3 = OneTimeWorkRequest.Builder(Worker3::class.java).build()

        workManager.beginWith(listOf(request1, request2)).then(request3).enqueue()
    }

    private fun observeAndEnqueue(request: WorkRequest) {
        workManager.enqueue(request)
        workId = request.id
        workManager.getWorkInfoByIdLiveData(request.id).observe(this) { status ->
            binding.txtStatus.text = when (status?.state) {
                WorkInfo.State.ENQUEUED -> "Enfileirado"
                WorkInfo.State.BLOCKED -> "Bloqueado"
                WorkInfo.State.CANCELLED -> "Cancelado"
                WorkInfo.State.RUNNING -> "Executando"
                WorkInfo.State.SUCCEEDED -> "Sucesso"
                WorkInfo.State.FAILED -> "Falhou"
                else -> "Indefinido"
            }

            binding.txtOutput.text = status?.outputData?.run {
                """${getString(MyWork.PARAM_NAME)} |${getInt(MyWork.PARAM_AGE, 0)} 
                    ||${getLong(MyWork.PARAM_TIME, 0)}""".trimMargin()
            }
        }
    }
}