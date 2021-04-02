package top.intkilow.jetpackbase.pages.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import top.intkilow.architecture.network.NetWorkManager
import top.intkilow.architecture.ui.BaseViewModel
import top.intkilow.architecture.utils.LogUtil
import top.intkilow.jetpackbase.api.TestApi

class HomeModel:BaseViewModel() {
    private val testApi = NetWorkManager.getInstance().getApiService(TestApi::class.java)

    val test:MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            viewModelScope.launch {
                // TODO: 此处演示请求 请替换自己的服务器
                val res = requestCustom {
                    testApi.testGet()
                }

                it.value = "此处演示请求 请替换自己的服务器"
            }
        }
    }




}