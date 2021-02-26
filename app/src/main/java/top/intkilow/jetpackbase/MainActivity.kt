package top.intkilow.jetpackbase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import top.intkilow.architecture.network.NetWorkManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetWorkManager.getInstance().init(HashMap(),"https://www.baidu.com/api/")
        setContentView(R.layout.activity_main)
    }
}