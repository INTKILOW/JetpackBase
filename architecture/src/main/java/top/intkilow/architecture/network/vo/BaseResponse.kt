package top.intkilow.architecture.network.vo

data class BaseResponse<out T>(val code: Int, val msg: String, val data: T?)