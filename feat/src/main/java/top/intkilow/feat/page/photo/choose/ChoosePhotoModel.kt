package top.intkilow.feat.page.photo.choose

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.intkilow.feat.vo.PhotoVO
import java.io.File
import java.util.*

class ChoosePhotoModel(application: Application) : AndroidViewModel(application) {

    val photoList = MutableLiveData<LinkedList<PhotoVO>>()

    val chooseSize = MutableLiveData<Int>()

    val selectSize = MutableLiveData(0)

    fun init() {
        if (null == photoList.value) {
            viewModelScope.launch {
                val queryImages = queryImages()
                queryImages.add(0, PhotoVO(Uri.parse(""), "", 0L))
                photoList.value = queryImages
            }
        }
    }


    /**
     * 查询图库
     */
    private suspend fun queryImages(): LinkedList<PhotoVO> {
        val photos = LinkedList<PhotoVO>()
        withContext(Dispatchers.IO) {

            val projection = arrayOf(
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns.SIZE
            )
            val selection = MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?"
            val selectionArgs = arrayOf("image/jpeg", "image/png")
            val sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC"

            getApplication<Application>().contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder,
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val idDisplayName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val idSize = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)


                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(idDisplayName)
                    val size = cursor.getLong(idSize)
                    // 通过ID 查询文件
                    val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                    )
                    // 生成相册图片vo
                    val photo = PhotoVO(contentUri, displayName, size)
                    photos += photo
                }
            }
        }
        return photos
    }

    /**
     * 保存图片到相册
     */
    fun saveToGallery(filePath: String): MutableLiveData<PhotoVO> {
        val photoVO = MutableLiveData<PhotoVO>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val data = toGallery(filePath)
                val vo = PhotoVO(data, "", 0L)
                photoList.value?.add(1, vo)
                photoVO.postValue(vo)
            }
        }
        return photoVO

    }

    private fun toGallery(filePath: String): Uri {

        getApplication<Application>().let { context ->

            val file = File(filePath)
            val values = ContentValues().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                    put(MediaStore.Audio.Media.IS_PENDING, 1)
                }
            }
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values)

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