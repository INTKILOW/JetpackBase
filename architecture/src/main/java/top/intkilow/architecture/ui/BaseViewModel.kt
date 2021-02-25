package top.intkilow.architecture.ui

import androidx.lifecycle.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import top.intkilow.architecture.network.vo.BaseResponse
import top.intkilow.architecture.network.vo.ErrorResp

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

    suspend fun <T : Any> requestCustom(
            call: suspend () -> T, responseSuccess: (response: T) -> Unit = {},
            responseError: (errorResp: ErrorResp) -> Unit = {}
    ) {
        try {
            responseSuccess(withContext(Dispatchers.IO) { call.invoke() })
        } catch (e: Exception) {
            var msg = "error"
            val code = 400
            e.message?.let {
                msg = it
            }
            val response = ErrorResp(code, msg)
            responseError(response)
        }
    }
}