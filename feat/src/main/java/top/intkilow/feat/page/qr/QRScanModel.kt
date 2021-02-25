package top.intkilow.feat.page.qr

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import top.intkilow.feat.qrscan.core.util.CodeUtils

class QRScanModel(application: Application) : AndroidViewModel(application) {

    val flashMode: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also {
            it.value = false
        }
    }
    val analysisFromPhoto: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().also {
            it.value = false
        }
    }

    fun parseQRFromUri(uri: Uri): MutableLiveData<String> {
        val data = MutableLiveData<String>()
        viewModelScope.launch {
            runInterruptible(Dispatchers.IO) {
                getApplication<Application>().contentResolver.openInputStream(uri)?.use {
                    val bitmap = BitmapFactory.decodeStream(it)
                    val result = CodeUtils.parseCode(bitmap)
                    data.postValue(result)
                }
            }
        }
        return data

    }
}