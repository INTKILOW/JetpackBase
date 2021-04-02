package top.intkilow.jetpackbase.pages.setting

import android.app.Application
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.intkilow.architecture.utils.DataStoreUtil
import top.intkilow.architecture.utils.LogUtil
import top.intkilow.jetpackbase.vo.Test

class SettingModel (application: Application) : AndroidViewModel(application) {

    val DATA_STORE = "JetpackBase"

    fun getDataStore() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dataStore = getApplication<Application>().createDataStore(DATA_STORE)
                val obj = DataStoreUtil.get(dataStore, "test", Test::class.java)
                LogUtil.e(obj)
            }
        }
    }

    fun setDataStore() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dataStore = getApplication<Application>().createDataStore(DATA_STORE)
                DataStoreUtil.set(
                    dataStore,
                    "test",
                    Test("wuji zhang", "188888888")
                )
            }
        }
    }
}