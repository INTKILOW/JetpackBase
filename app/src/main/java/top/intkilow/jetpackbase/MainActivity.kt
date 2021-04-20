package top.intkilow.jetpackbase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import top.intkilow.architecture.network.NetWorkManager
import top.intkilow.architecture.utils.StatusBarUtil
import top.intkilow.jetpackbase.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetWorkManager.getInstance().init(HashMap(), "https://www.baidu.com/api/")


        StatusBarUtil.fixStatusBar(window)

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

    }
}