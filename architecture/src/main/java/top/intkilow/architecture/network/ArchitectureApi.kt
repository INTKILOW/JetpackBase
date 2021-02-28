package top.intkilow.architecture.network

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.POST
import top.intkilow.architecture.network.vo.BaseResponse
interface ArchitectureApi {
    /**
     * 上传图片
     */
    @POST("upload")
    suspend fun uploadFile(@Body multipartBody: MultipartBody): BaseResponse<List<String>>
}