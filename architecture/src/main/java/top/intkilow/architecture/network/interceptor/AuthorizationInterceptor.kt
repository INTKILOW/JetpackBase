package top.intkilow.architecture.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {

    var token: String = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        var newRequest = chain.request()
        newRequest = newRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        return chain.proceed(newRequest)
    }
}