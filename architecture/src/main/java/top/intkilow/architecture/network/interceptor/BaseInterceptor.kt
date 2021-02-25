package top.intkilow.architecture.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


/**
 *  公共参数拦截器
 *
 */
class BaseInterceptor(var mParamsMap: Map<String, String>?) : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val urlBuilder = chain.request().url.newBuilder()
        val requestBuilder = chain.request().newBuilder()

        mParamsMap?.let {
            for ((key, value) in it) {
                requestBuilder.addHeader(key, value)
            }
        }
        val newRequest = requestBuilder
                .url(urlBuilder.build())
                .build()
        return chain.proceed(newRequest)
    }


}
