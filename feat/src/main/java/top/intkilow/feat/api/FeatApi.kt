package top.intkilow.feat.api

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.POST
import top.intkilow.architecture.network.vo.BaseResponse
import top.intkilow.feat.vo.FileUrlVO

interface FeatApi {


    @POST("upload")
    suspend fun uploadFile(@Body multipartBody: MultipartBody): BaseResponse<FileUrlVO>


}