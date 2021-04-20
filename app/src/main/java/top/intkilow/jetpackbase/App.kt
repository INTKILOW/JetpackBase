package top.intkilow.jetpackbase

import android.app.Application
import top.intkilow.architecture.network.NetWorkManager
import top.intkilow.feat.FeatCore

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化 网络
        NetWorkManager.getInstance().init(HashMap(),"https://www.baidu.com")
        FeatCore.init()
    }
}