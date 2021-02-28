"# JetpackBase" 

快速开发，专注业务
navigation、databinding、viewmodel、retroift、okhttp、协程、paging、datastore...

注意:

1、使用网络请求先初始化网络
NetWorkManager.getInstance().init(HashMap(),"https://www.baidu.com/api/")

2、上传文件默认地址为upload不可修改，返回实体为:BaseResponse<List<String>>
    @POST("upload")
    suspend fun uploadFile(@Body multipartBody: MultipartBody): BaseResponse<List<String>>


3、基础http回调实体结构
data class BaseResponse<out T>(val code: Int, val msg: String, val data: T?)

4、分页pagingDataSource（T里面包含这个实体）
data class RefreshResponse<out T>(val page: Int, val hasNextPage: Boolean, val list: List<T>)
分页基础参数
a、page（自动计算）
b、pageSize（默认为空）

5、防止连续点击方法使用:binding.scan.setOnClickDebounced {}

6、ViewUtils使用
a、dp转px
b、sp转px
c、屏幕宽高参数
d、获取状态栏高度
e、使view圆角

7、feat库页面（先跳转SpringboardPage，填入相对应的页面，页面值在Constants.kt）
a、相册选择照片
b、扫描二维码
c、图片预览
d、web