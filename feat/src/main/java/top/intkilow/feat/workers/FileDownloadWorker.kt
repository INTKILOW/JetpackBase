package top.intkilow.feat.workers

import android.content.Context
import android.text.TextUtils
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import top.intkilow.architecture.network.ArchitectureApi
import top.intkilow.architecture.network.NetWorkManager
import java.io.File
import java.io.FileOutputStream


class FileDownloadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    var filePath = File.separator + "download"

    override suspend fun doWork(): Result {

        val url = inputData.getString("url") ?: ""
        if (TextUtils.isEmpty(url)) {
            return Result.failure(
                Data.Builder()
                    .putString("msg", "url == null")
                    .build()
            )
        }
        val call = NetWorkManager.getInstance().getApiService(ArchitectureApi::class.java)
            .downloadFile(url)
         call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val newPath = applicationContext.filesDir.path + filePath
                val file = File(newPath)
                if (!file.exists()) {
                    file.createNewFile()
                }
                var msg = "download ok!"
                response.body()?.byteStream()?.use { inputStream ->
                    FileOutputStream(file).use { fos ->
                        val total = response.body()?.contentLength() ?: 0L
                        try {
                            var len = -1
                            val buf = ByteArray(2048)
                            var sum: Long = 0
                            var start = System.currentTimeMillis()

                            while (inputStream.read(buf).also { len = it } != -1) {
                                fos.write(buf, 0, len)
                                sum += len.toLong()
                                val end = System.currentTimeMillis()
                                if (end - start >= 500) {
                                    start = end
                                    val progress = (sum * 1.0f / total * 100).toInt()
                                    //下载中更新进度条
                                }
                            }
                            fos.flush()
                        } catch (e: Exception) {
                            msg = e.message?:"download fail!"
                        }
                        fos.close()

                    }
                    inputStream.close()
                }
                // 下载完成

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }
        })

       return Result.failure()

    }
}



