package top.intkilow.architecture.paging

import androidx.paging.PagingSource
import top.intkilow.architecture.network.vo.BaseResponse
import top.intkilow.architecture.network.vo.RefreshResponse

/**
 * 分页加载 数据源
 */
abstract class PagingDataSource<T : Any> : PagingSource<HashMap<String, Any>, T>() {


    override suspend fun load(params: LoadParams<HashMap<String, Any>>): LoadResult<HashMap<String, Any>, T> {

        return try {


            val pageNow = params.key?.get("page") ?: 1

            val pageSize = params.loadSize
            val map = HashMap<String, Any>()
            params.key?.let { map.putAll(it) }


            map["page"] = pageNow
            map["pageSize"] = pageSize

            val result = getService(map)

            var nextKey: HashMap<String, Any>? = null

            val data = result.data

            data?.let {
                if (data.hasNextPage) {
                    map["page"] = map["page"].toString().toInt() + 1
                    nextKey = map
                }
                LoadResult.Page(
                        data = data.list,
                        prevKey = null,
                        nextKey = nextKey
                )
            } ?: LoadResult.Error(Exception("data == null"))
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }


    abstract suspend fun getService(map: HashMap<String, Any>): BaseResponse<RefreshResponse<T>>


}