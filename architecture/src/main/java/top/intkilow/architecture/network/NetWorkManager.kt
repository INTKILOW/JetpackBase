package top.intkilow.architecture.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import top.intkilow.architecture.BuildConfig
import top.intkilow.architecture.network.interceptor.AuthorizationInterceptor
import top.intkilow.architecture.network.interceptor.BaseInterceptor
import top.intkilow.architecture.network.interceptor.HttpLogger
import java.util.concurrent.TimeUnit


class NetWorkManager {
    private var retrofit: Retrofit? = null
    private val authorizationInterceptor = AuthorizationInterceptor()


    companion object {

        @Volatile
        private var instance: NetWorkManager? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: NetWorkManager().also { instance = it }
            }
    }

    fun init(mParamsMap: Map<String, String>, baseUrl: String = "") {


        // 初始化okhttp
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .addInterceptor(authorizationInterceptor)
            .addInterceptor(BaseInterceptor(mParamsMap))
            .readTimeout(1000 * 20.toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout(1000 * 20.toLong(), TimeUnit.MILLISECONDS)
            .connectTimeout(1000 * 20.toLong(), TimeUnit.MILLISECONDS)

        if (BuildConfig.DEBUG) {
            val logInterceptor = HttpLoggingInterceptor(HttpLogger())
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(logInterceptor)
        }


        val client = builder.build()
        retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        // 初始化Retrofit
    }

    fun updateToken(token: String) {
        authorizationInterceptor.token = token
    }


    fun <T> getApiService(clazz: Class<T>): T {
        if (null == retrofit) {
            throw Exception("请先初始化 OkHttpClient !")
        }
        return retrofit!!.create(clazz)
    }
}