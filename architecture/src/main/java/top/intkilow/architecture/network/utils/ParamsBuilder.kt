package top.intkilow.architecture.network.utils

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

class ParamsBuilder {
    companion object {
        fun builder(): Builder {
            return Builder()
        }

        class Builder {
            private val params = HashMap<String, Any>()
            fun put(key: String, value: Any): Builder {
                params[key] = value
                return this
            }

            fun put(map: Map<String, Any>): Builder {
                params.putAll(map)
                return this
            }

            fun build(): String {
                return Gson().toJson(params)
            }

            fun buildBody(): RequestBody {
                return build().toRequestBody(getMediaType())
            }
        }

        fun buildBodyByMap(map: HashMap<String, Any>): RequestBody {
            return Gson().toJson(map).toRequestBody(getMediaType())
        }

        fun buildBodyByJsonString(json: String): RequestBody {
            return json.toRequestBody(getMediaType())
        }

        fun getMediaType(): MediaType {
            return "application/json;charset=utf-8".toMediaType()
        }
    }

}