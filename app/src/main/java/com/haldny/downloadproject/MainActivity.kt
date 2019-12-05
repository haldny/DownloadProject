package com.haldny.downloadproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var asyncTask: MyAsyncTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        asyncTask = MyAsyncTask(this, object : TaskListener {
            override fun onTaskComplete(s: String) {
                textView3.text = s
            }
        })
    }

    override fun onResume() {
        super.onResume()

        button2.setOnClickListener { asyncTask.execute(
            URL("https://api.github.com/search/repositories?q=language:Java&sort=stars"))  }

        button3.setOnClickListener {
            val workManager = WorkManager.getInstance()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build()

            val inputData = Data.Builder()
                .putString("url",
                    "http://www.omdbapi.com/?t=Game%20of%20Thrones&Season=1&apikey=59317c2e").build()

            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
                .setConstraints(constraints).setInputData(inputData).build()

            workManager.enqueue(oneTimeWorkRequest)

            workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.id).
                observe(this, Observer { workInfo ->
                    if (workInfo != null && workInfo.state.isFinished) {
                        textView3.text = workInfo.outputData.getString("json")
                    }
            })
        }
    }
}
