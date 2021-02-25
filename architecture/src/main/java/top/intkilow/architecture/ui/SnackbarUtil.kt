package top.intkilow.architecture.ui

import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

class SnackbarUtil {
    companion object {

        fun toast(view: View, msg: String) {
            toast(view, msg, Snackbar.LENGTH_SHORT)
        }
        fun toast(view: View, res: Int) {
            toast(view, res, Snackbar.LENGTH_SHORT)
        }

        fun toast(view: View, msg: String, duration: Int) {

            try {
                Snackbar.make(view, msg, duration).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        fun toast(view: View, res: Int, duration: Int) {

            try {
                Snackbar.make(view, res, duration).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}