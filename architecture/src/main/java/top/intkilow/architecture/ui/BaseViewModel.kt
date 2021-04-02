package top.intkilow.architecture.ui

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import top.intkilow.architecture.network.vo.BaseResponse
import top.intkilow.architecture.network.vo.ErrorResp
import top.intkilow.architecture.utils.LogUtil

open class BaseViewModel : ViewModel(), LifecycleObserver {

    suspend fun <T : Any> request(call: suspend () -> BaseResponse<T>): BaseResponse<T> {
        return try {
            withContext(Dispatchers.IO) { call.invoke() }
        } catch (e: Exception) {
            var msg = "error"
            var code = 400
            e.message?.let {
                msg = it
            }
            if (e is HttpException) {
                code = e.code()
            }
            BaseResponse(code, msg, null)
        }

    }
    suspend fun <T : Any> requestCustom(call: suspend () -> T): T? {
        return try {
            withContext(Dispatchers.IO) { call.invoke() }
        } catch (e: Exception) {
            LogUtil.e(e,"requestCustom")
           null
        }

    }
}