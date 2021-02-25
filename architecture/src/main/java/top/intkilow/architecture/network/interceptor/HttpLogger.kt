package top.intkilow.architecture.network.interceptor

import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor

class HttpLogger :HttpLoggingInterceptor.Logger{
    override fun log(message: String) {
        Log.e("TAG",message)
    }

}