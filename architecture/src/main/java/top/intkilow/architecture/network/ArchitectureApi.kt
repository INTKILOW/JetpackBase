package top.intkilow.architecture.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url


interface ArchitectureApi {


    /**
     * 文件下载
     */
    @Streaming
    @GET
    suspend fun downloadFile(@Url url: String): Call<ResponseBody>

}