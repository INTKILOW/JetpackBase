package top.intkilow.architecture.utils

import android.content.Context
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider

class ViewUtils {
    companion object {
        // 主色调
        var colorPrimary: Int = 0xFFFF5722.toInt()

        // 是否需要状态栏
        var needStatusBar: Boolean = true

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
         * 获取 drawable
         */
        fun getGradientDrawable(
            context: Context,
            backgroundColor: Int,
            shape: Int,
            corner: Float = 4.0f,
            corners: FloatArray? = null,
            strokeColor: Int? = null,
            strokeWidth: Int? = null,
        ): GradientDrawable {
            //创建Drawable对象
            val drawable = GradientDrawable()
            //设置背景色
            drawable.setColor(backgroundColor)
            //设置圆角的半径  
            if (null != corners) {
                for (i in corners.indices) {
                    val dp2px = dp2px(context, corners[i])
                    corners[i] = dp2px.toFloat()
                }
                drawable.cornerRadii = corners
            } else {
                drawable.cornerRadius = dp2px(context, corner).toFloat()
            }
            if (strokeWidth != null && strokeColor != null) {
                drawable.setStroke(strokeWidth, strokeColor)
            }
            //设置shape形状
            drawable.shape = shape
            return drawable
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