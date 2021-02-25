package top.intkilow.feat.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import top.intkilow.architecture.network.NetWorkManager
import top.intkilow.feat.api.FeatApi
import java.io.ByteArrayOutputStream

/**
 * 上传图片任务
 */
class FileUploadWorker(context: Context, workerParams: WorkerParameters)
    : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val fileUris = inputData.getStringArray("fileUris")
        // 获取文件
        val fileType = inputData.getString("fileType") ?: "img"
        return try {
            val builder = MultipartBody.Builder()
            fileUris?.forEach {
                val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)


                val uri = Uri.parse(it)
                var fileName = "${System.currentTimeMillis()}.png"
                applicationContext.contentResolver.query(uri, projection,
                        null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val idDisplayName =
                                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                        fileName = cursor.getString(idDisplayName)
                    }
                }
                applicationContext.contentResolver.openInputStream(uri)?.use { inputStream ->


                    // 不压缩
                    val fileBytes = if (fileName.endsWith(".png")
                            || fileName.endsWith(".jpg")) {
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
            val result = NetWorkManager.getInstance().getApiService(FeatApi::class.java)
                    .uploadFile(multipartBody)
            result.data?.let { fileVO ->
                val array = fileVO.url.toTypedArray()
                val data = Data.Builder()
                        .putStringArray("urls", array)
                        .build()
                return Result.success(data)
            }


            Result.failure()
        } catch (e: Exception) {
            Result.failure()
        }


    }
}