package top.intkilow.feat.page.preview

import androidx.lifecycle.MutableLiveData
import top.intkilow.architecture.ui.BaseViewModel

class PreviewModel : BaseViewModel() {
    val current: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().also {
            it.value = 1
        }
    }

    val total: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().also {
            it.value = 0
        }
    }

}