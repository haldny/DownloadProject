package com.haldny.downloadproject

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

interface TaskListener {

    fun onTaskComplete(s: String)

}

class MyAsyncTask(private val mContext: Context, private val mListener: TaskListener) :
    AsyncTask<URL, Void, String>() {

    private var urlConnection: HttpURLConnection? = null
    private val mDialog: ProgressDialog

    init {
        mDialog = ProgressDialog(mContext)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        mDialog.setTitle(R.string.app_name)
        mDialog.setMessage("Retrieving data...")
        mDialog.show()
    }

    override fun doInBackground(vararg params: URL): String {
        val result = StringBuilder()

        try {
            val url = params[0]
            urlConnection = url.openConnection() as HttpURLConnection?
            urlConnection?.doInput = true
            urlConnection?.connectTimeout = 20 * 1000
            urlConnection?.readTimeout = 20 * 1000

            if (urlConnection?.responseCode == HttpURLConnection.HTTP_OK) {

                val `in` = BufferedInputStream(urlConnection?.inputStream)
                val reader = BufferedReader(InputStreamReader(`in`))

                var line : String?

                do {
                    line = reader.readLine()
                    if (line == null)
                        break

                    result.append(line)
                } while (true)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }


        return result.toString()
    }

    override fun onPostExecute(s: String) {
        super.onPostExecute(s)
        mDialog.dismiss()
        mListener.onTaskComplete(s)
    }
}
