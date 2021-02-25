package top.intkilow.architecture.network.vo

data class RefreshResponse<out T>(val page: Int, val hasNextPage: Boolean, val list: List<T>)