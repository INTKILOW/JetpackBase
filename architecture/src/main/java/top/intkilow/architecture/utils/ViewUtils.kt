package top.intkilow.architecture.utils

import android.content.Context
import android.graphics.Outline
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider

class ViewUtils {
    companion object {
        /**
         * 获取屏幕宽高
         * [0,0]
         */
        fun getScreenWH(context: Context): IntArray {
            val widthPixels = context.resources.displayMetrics.widthPixels
            val heightPixels = context.resources.displayMetrics.heightPixels
            return intArrayOf(widthPixels, heightPixels)
        }

        /**
         * 获取状态栏高度
         */
        fun getStatusBarHeight(context: Context): Int {
            val identifier = context.resources.getIdentifier(
                "status_bar_height",
                "dimen", "android"
            )
            if (identifier > 0) {
                return context.resources.getDimensionPixelSize(identifier)
            }

            return 0

        }

        /**
         * dp转px
         */
        fun dp2px(context: Context, dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp, context.resources.displayMetrics
            ).toInt()
        }

        /**
         * sp转px
         */
        fun sp2px(context: Context, sp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp, context.resources.displayMetrics
            ).toInt()
        }

        /**
         * 使view圆角
         */
        fun clipToOutlineView(view: View, radius: Float) {
            val r: Int = dp2px(view.context, radius)
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