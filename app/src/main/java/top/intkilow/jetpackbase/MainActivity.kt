package top.intkilow.jetpackbase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.contains
import androidx.databinding.DataBindingUtil
import top.intkilow.architecture.network.NetWorkManager
import top.intkilow.jetpackbase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetWorkManager.getInstance().init(HashMap(), "https://www.baidu.com/api/")

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

/*
        val FLAGS_FULLSCREEN =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        binding.navHost.systemUiVisibility = FLAGS_FULLSCREEN*/
//        ActivityMainBinding.
    }
}