package top.intkilow.architecture.utils

import android.util.Log
import com.google.gson.GsonBuilder

class LogUtil {
    companion object {
        var debug = true
        fun <T> v(msg: T, tag: String = "") {
            log(0, msg, message = tag)
        }

        fun <T> d(msg: T, tag: String = "") {
            log(1, msg, message = tag)
        }

        fun <T> i(msg: T, tag: String = "") {
            log(2, msg, message = tag)
        }

        fun <T> w(msg: T, tag: String = "") {
            log(3, msg, message = tag)
        }

        fun <T> e(msg: T, tag: String = "") {
            log(4, msg, message = tag)
        }


        fun block(call: () -> Unit) {
            if (!debug) {
                return
            }
            try {
                call.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        private fun <T> log(
            type: Int,
            data: T,
            tag: String = "TAG",
            message: String = ""
        ) {
            if (!debug) {
                return
            }
            var msg = GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(data)
            msg = "************************************\n$msg"
            if (message.isNotEmpty()) {
                msg = "| $message |$msg"
            }
            // v d i w e
            when (type) {
                0 -> {
                    Log.v(tag, msg)
                }
                1 -> {
                    Log.d(tag, msg)
                }
                2 -> {
                    Log.i(tag, msg)
                }
                3 -> {
                    Log.w(tag, msg)
                }
                4 -> {
                    Log.e(tag, msg)
                }
            }

        }

    }
}