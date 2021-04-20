package top.intkilow.architecture.utils

import android.graphics.Color
import android.view.View
import android.view.Window
import android.view.WindowManager

class StatusBarUtil {
    companion object {

        var lastStatusColor: Boolean? = null

        @Suppress("DEPRECATION")
        fun fixStatusBar(window: Window?) {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                     or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.statusBarColor = Color.TRANSPARENT
        }

        @Suppress("DEPRECATION")
        fun setStatusBarColor(window: Window?, dark: Boolean = true) {
            if (lastStatusColor == dark) {
                return
            }
            lastStatusColor = dark
            if (dark) {
                window?.decorView?.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window?.decorView?.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }

    }
}