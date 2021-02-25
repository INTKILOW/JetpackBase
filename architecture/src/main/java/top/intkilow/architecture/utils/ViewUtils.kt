package top.intkilow.architecture.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

class ViewUtils {
    companion object {
        fun clipToOutlineView(view: View, radius: Float) {
            val r: Int = DisplayUtil.dp2px(view.context, radius)
            //获取outline
            val viewOutlineProvider: ViewOutlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    //修改outline为特定形状,上下左右相当于padding
                    outline.setRoundRect(0, 0, view.width, view.height, r.toFloat())
                }
            }
            view.outlineProvider = viewOutlineProvider
            view.clipToOutline = true
        }
    }
}