package top.intkilow.jetpackbase.pages.main

import androidx.lifecycle.MutableLiveData
import top.intkilow.architecture.ui.BaseViewModel

class MainModel: BaseViewModel() {
    val currentIndex = MutableLiveData(0)

}