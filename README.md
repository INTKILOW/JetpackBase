"# JetpackBase" 

快速开发，专注业务  
navigation、databinding、viewmodel、retroift、okhttp、协程、paging、datastore...

# 如何使用（最新版本以当前为准）
```kotlin
    // Step 1. Add the JitPack repository to your build file
    allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
```
```kotlin
    // Step 2. Add the dependency
	dependencies {
	    implementation 'com.github.INTKILOW:JetpackBase:0.0.1-alpha03'
	}
```

# 注意:

## 1、使用网络请求先初始化网络
```kotlin
    NetWorkManager.getInstance().init(HashMap(),"https://www.baidu.com/api/")
    NetWorkManager.getInstance().updateToken("")
```
## 2、上传文件默认地址为upload不可修改
```kotlin
    @POST("upload")
    suspend fun uploadFile(@Body multipartBody: MultipartBody): BaseResponse<List<String>>
```
## 3、基础http回调实体结构
```kotlin
    data class BaseResponse<out T>(val code: Int, val msg: String, val data: T?)
```
```kotlin
    data class RefreshResponse<out T>(val page: Int, val hasNextPage: Boolean, val list: List<T>)
```
## 4、分页pagingDataSource（T里面包含这个实体）

    分页基础参数  
    a、page（自动计算）  
    b、pageSize（默认为空）  
### viewmodel
```kotlin
val data: MutableLiveData<PagingData<T>> by lazy {
    MutableLiveData<PagingData<T>>().also {
        viewModelScope.launch {
            getPageData().collectLatest { value: PagingData<T> ->
                it.value = value
            }
        }
    }
}
```
```kotlin
    suspend fun getPageData(): Flow<PagingData<T>> {

        return Pager(PagingConfig(pageSize = 10)) {
            object : PagingDataSource<T>() {
                override suspend fun getService(map: HashMap<String, Any>):
                        BaseResponse<RefreshResponse<T>> {
                    map["id"] = 0
                    val body = ParamsBuilder
                            .builder()
                            .put(map)
                            .buildBody()
                    return mainApi.paging(body)
                }
            }
        }.flow.cachedIn(viewModelScope)
    }

```
### fragment
```kotlin
    adapter.addLoadStateListener {
        when (it.refresh) {
            is LoadState.Error -> {
                binding.refreshLayout.isRefreshing = false
            }
            is LoadState.NotLoading -> {
                binding.refreshLayout.isRefreshing = false
            }
            else -> {

            }
        }
    }
    model.data.observe(viewLifecycleOwner, {
        adapter.submitData(lifecycle, it)
    })

    binding.recyclerview.adapter = adapter.withLoadStateFooter(PagingLoadStateAdapter(adapter))

    binding.refreshLayout.setOnRefreshListener {
        adapter.refresh()
    }
```
### adapter
```kotlin
class XXXAdapter(val onImageClick: (position: Int, urls: ArrayList<String>) -> Unit) :
        PagingDataAdapter<T, ViewHolder>(PagingDiff.getDefaultDiff<T>()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = DataBindingUtil
                .getBinding<xxxxBinding>(holder.itemView)
        binding?.let {
            val t = getItem(position)
            t?.let { vo ->
                // TODO
            }
            it.executePendingBindings()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = xxxxBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

}
```

## 5、防止连续点击方法使用:
```kotlin
    binding.scan.setOnClickDebounced {
        // TODO
    }
```
## 6、ViewUtils使用
```kotlin
    a、dp转px  
    b、sp转px  
    c、屏幕宽高参数  
    d、获取状态栏高度  
    e、使view圆角  
    ……
```
## 7、feat库页面（先跳转SpringboardPage，填入相对应的页面，页面值在Constants.kt）
```kotlin
    // 相册选择照片

    findNavController().navigate(
        R.id.springboard,
        Bundle().apply {
            putString(
                PAGE,
                CHOOSE_PHOTO_PAGE
            )
            putInt("max", 4)
            putInt("destinationId", -1)
        }, NavControllerHelper.getNavOptions()
    )

    // onCreateView先设置此方法 选择完成照片后 会自动回调
     NavControllerHelper.getSavedStateHandle<List<PhotoVO>>(
            this,
            CHOOSE_PHOTO_RESULT_DATA,
            result = {
                // TODO
            },
            viewLifecycleOwner
        )

```
```kotlin
    // 扫描二维码
     findNavController().navigate(
        R.id.springboard,
        Bundle().apply {
            putString(
                PAGE,
                SCAN_PAGE
            )
        }, NavControllerHelper.getNavOptions()
    )

    NavControllerHelper.getSavedStateHandle<String>(
        this,
        SCAN_RESULT_DATA,
        result = {
            // TODO
        },
        viewLifecycleOwner
    )
```
```kotlin
    // 图片预览
    val arr = ArrayList<String>()
    arr.add("https://xxxxx/xxx.png")
    arr.add("https://xxxxx/xxx.png") 
    findNavController().navigate(
        R.id.springboard,
        Bundle().apply {
            putString(
                PAGE,
                PHOTO_PREVIEW_PAGE
            )
            putStringArrayList("images",arr)
            putInt("current",1)
        }, NavControllerHelper.getNavOptions()
    )
```
```kotlin
    // WEB
    findNavController().navigate(
        R.id.springboard,
        Bundle().apply {
            putString(
                PAGE,
                WEB_PAGE
            )
            putString("content", "https://intkilow.top")
        }, NavControllerHelper.getNavOptions()
    )
```
## 8、本地存储 dataStore
```kotlin
    suspend fun <T> set(dataStore: DataStore<Preferences>?, key: String, data: T) {
        val msg = Gson().toJson(data)
        val DATA = preferencesKey<String>(key)
        dataStore?.edit {
            it[DATA] = msg
        }
    }

    suspend fun <T> get(dataStore: DataStore<Preferences>?, key: String, clazz: Class<T>): T? {
        val DATA = preferencesKey<String>(key)
        val result = dataStore?.data?.map { preferences ->
            preferences[DATA] ?: ""
        }?.first() ?: ""

        if (result.isNotEmpty()) {
            return Gson().fromJson(result, clazz)
        }
        return null
    }
```
```kotlin
    // 对于token 的单独操作
    suspend fun setToken(dataStore: DataStore<Preferences>?, token: String, tokenTime: Long) {

        val TOKEN = preferencesKey<String>(TOKEN)
        val EXPIRES_TIME = preferencesKey<Long>(EXPIRES_TIME)
        val TOKEN_TIME = preferencesKey<Long>(TOKEN_TIME)

        dataStore?.edit {
            it[TOKEN] = token
            it[EXPIRES_TIME] = tokenTime
            it[TOKEN_TIME] = System.currentTimeMillis()
        }

    }

    suspend fun getToken(dataStore: DataStore<Preferences>?): String {
        return dataStore?.let {
            val TOKEN = preferencesKey<String>(TOKEN)
            val token = dataStore.data.map { preferences ->
                preferences[TOKEN] ?: BASE_TOKEN
            }.first()

            if (token.isNotEmpty()) {
                // 判断token
                val EXPIRES_TIME = preferencesKey<Long>(EXPIRES_TIME)
                val TOKEN_TIME = preferencesKey<Long>(TOKEN_TIME)
                val tokenTime = dataStore.data.map { preferences ->
                    preferences[TOKEN_TIME] ?: 0L
                }.first()

                val expiresTime = dataStore.data.map { preferences ->
                    preferences[EXPIRES_TIME] ?: 0L
                }.first()

                if (System.currentTimeMillis() - tokenTime > expiresTime * 1000) {
                    // 清除本地用户缓存数据
                    setToken(dataStore, "", 0L)
                } else {
                    return token
                }
            }
            return BASE_TOKEN
        } ?: BASE_TOKEN

    }
```

## 9、LogUtil 打印日志
```kotlin
    // 发布后不会执行
    
    // 普通打印日志 
    LogUtil.e("0000000000000000")
    LogUtil.e("0000000000000000","wuji zhang")
    // 块打印日志
    LogUtil.block {
        LogUtil.e("0000000000000000")
        LogUtil.e("111111")
        LogUtil.e("22222")
        LogUtil.e("333")
    }

```
