package top.intkilow.architecture.network.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import top.intkilow.architecture.network.ArchitectureApi
import top.intkilow.architecture.network.NetWorkManager
import top.intkilow.architecture.network.vo.BaseResponse
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class FileTransfer {
    companion object {


        /**
         * 文件上传
         */
        suspend fun upload(
            // 相册uri
            uris: ArrayList<String>,
            context: Context,
            fileType: String = "img"
        ): BaseResponse<List<String>> {

            val builder = MultipartBody.Builder()
            uris.forEach { uriStr ->
                val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)

                val uri = Uri.parse(uriStr)
                var fileName = "${System.currentTimeMillis()}.png"
                context.contentResolver.query(
                    uri, projection,
                    null, null, null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val idDisplayName =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                        fileName = cursor.getString(idDisplayName)
                    }
                }
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    // 不压缩
                    val fileBytes = if (fileName.endsWith(".png")
                        || fileName.endsWith(".jpg")
                    ) {
                        // 当是图片 要压缩
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val bos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        var options = 100
                        // 阈值 512KB
                        while (bos.toByteArray().size / 1024 > 1546) {
                            bos.reset()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, options, bos)
                            options -= 24
                        }
                        bos.toByteArray()
                    } else {
                        inputStream.readBytes()
                    }
                    val requestBody = fileBytes.toRequestBody("multipart/form-data".toMediaType())
                    builder.addFormDataPart("files", fileName, requestBody)
                    inputStream.close()
                }
            }

            builder.addFormDataPart("filePath", fileType)
            builder.setType(MultipartBody.FORM)
            val multipartBody = builder.build()
            return NetWorkManager.getInstance().getApiService(ArchitectureApi::class.java)
                .uploadFile(multipartBody)
        }

        /**
         * 文件下载
         * url 文件下载路径
         *
         */
        fun download(
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

            val request: Request = Request.Builder()
                .url(url)
                .build()
            val client: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout((1000 * 5).toLong(), TimeUnit.MILLISECONDS)
                .build()

            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    result(null, e.message ?: errMsg)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val newPath = context.filesDir.path + filePath
                    val parentDir = File(newPath)
                    if (!parentDir.exists()) {
                        parentDir.mkdirs()
                    }
                    val file = File(newPath + File.separator + fileName)
                    var msg = successMsg

                    response.body?.byteStream()?.use { inputStream ->
                        FileOutputStream(file).use { fos ->
                            val total = response.body?.contentLength() ?: 0L
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