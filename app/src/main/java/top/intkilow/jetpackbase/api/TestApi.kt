package top.intkilow.jetpackbase.api

import retrofit2.http.GET
import top.intkilow.architecture.network.vo.BaseResponse

interface TestApi {

    @GET("http://www.baidu.com")
    suspend fun testGet(): String

}