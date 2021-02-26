package top.intkilow.architecture.network.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import top.intkilow.architecture.network.ArchitectureApi
import top.intkilow.architecture.network.NetWorkManager
import java.io.File
import java.io.FileOutputStream

class FileTransfer {
    companion object {
        /**
         * 文件下载
         * url 文件下载路径
         *
         */
        suspend fun download(
            url: String,
            context: Context,
            fileName: String = "${System.currentTimeMillis()}.png",
            result: (file: File?, msg: String) -> Unit = { _, _ -> },
            progressCall: (progress: Int) -> Unit = {},
            saveAlbum: Boolean = true,
            filePath: String = File.separator + "download"
        ) {
            val errMsg = "download fail!"
            val successMsg = "download ok!"
            val call = NetWorkManager.getInstance().getApiService(ArchitectureApi::class.java)
                .downloadFile(url)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val newPath = context.filesDir.path + filePath
                    val parentDir = File(newPath)
                    if (!parentDir.exists()) {
                        parentDir.mkdirs()
                    }
                    val file = File(newPath + File.separator + fileName)
                    var msg = successMsg
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
                                        progressCall(progress)
                                    }
                                }
                                fos.flush()
                            } catch (e: Exception) {
                                msg = e.message ?: errMsg
                            }
                            fos.close()

                        }
                        inputStream.close()
                    }
                    if (saveAlbum && msg == successMsg &&
                        (fileName.endsWith(".png") || fileName.endsWith(".jpg"))
                    ) {
                        // 将文件存到相册
                        toGallery(file, context)
                    }
                    // 下载完成 但不一定成功
                    result(file, msg)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    result(null, t.message ?: errMsg)
                }
            })

        }


        /**
         * 将文件保存相册
         */
        fun toGallery(file: File, context: Context): Uri {

            val values = ContentValues().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                    put(MediaStore.Audio.Media.IS_PENDING, 1)
                }
            }
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )

            context.contentResolver.openOutputStream(uri!!).use { out ->
                out?.let {
                    val fileUri = Uri.fromFile(file)
                    context.contentResolver.openInputStream(fileUri).use { input ->
                        input?.let {
                            out.write(input.readBytes())
                        }
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Audio.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
            }
            file.delete()

            return uri
        }
    }
}