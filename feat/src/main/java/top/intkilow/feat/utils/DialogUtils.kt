package top.intkilow.feat.utils

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItemsSingleChoice

class DialogUtils {


    companion object {

        /**
         * 单选列表
         * etc
         * 返回null 为未选择
         */
        fun <T : CharSequence> showSingleChoice(context: Context, title: String,
                                                data: List<T>,
                                                initialSelection: Int = -1,
                                                owner: LifecycleOwner? = null,
                                                call: (item: T?) -> Unit = {}) {
            MaterialDialog(context).show {
                var d: T? = null
                title(text = title)
                listItemsSingleChoice(items = data, initialSelection = initialSelection)
                { dialog, index, _ ->
                    // 选中数据
                    d = data[index]
                    dialog.dismiss()
                }
                positiveButton()
                negativeButton()
                cancelOnTouchOutside(false)
                cancelable(false)
                setOnDismissListener {
                    call(d)
                }
                lifecycleOwner(owner)
            }
        }





    }
}