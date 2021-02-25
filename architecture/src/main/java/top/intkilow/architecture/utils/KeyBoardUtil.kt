package top.intkilow.architecture.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


class KeyBoardUtil {

    companion object {
        /**
         * 隐藏键盘
         * 弹窗弹出的时候把键盘隐藏掉
         */
        fun hideInputKeyboard(v: View) {
            val imm: InputMethodManager? =
                v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(v.getWindowToken(), 0)
        }

        /**
         * 弹起键盘
         */
        fun showInputKeyboard(v: View) {
            val imm: InputMethodManager? =
                v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.showSoftInput(v, 0)
        }
    }


}