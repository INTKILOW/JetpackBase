package top.intkilow.architecture.network.interceptor
import okhttp3.logging.HttpLoggingInterceptor
import top.intkilow.architecture.utils.LogUtil

class HttpLogger :HttpLoggingInterceptor.Logger{
    override fun log(message: String) {
        LogUtil.e(message)
    }

}