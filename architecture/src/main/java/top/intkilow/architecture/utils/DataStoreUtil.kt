package top.intkilow.architecture.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import top.intkilow.architecture.constant.EXPIRES_TIME
import top.intkilow.architecture.constant.TOKEN
import top.intkilow.architecture.constant.TOKEN_TIME

class DataStoreUtil {
    companion object {
        // 使用前请先设置baseToken
        var BASE_TOKEN = ""

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
    }
}