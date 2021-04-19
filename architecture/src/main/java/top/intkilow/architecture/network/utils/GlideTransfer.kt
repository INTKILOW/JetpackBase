package top.intkilow.architecture.network.utils

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.target.Target

class GlideTransfer {
    companion object{
        /**
         * bitmap 通过url 获取bitmap
         * Do something with the Bitmap and then when you're done with it:
         * Glide.with(context).clear(futureTarget);
         */
        fun getBitmapFromUrl(
            context: Context,
            url: String,
            width: Int = Target.SIZE_ORIGINAL,
            height: Int = Target.SIZE_ORIGINAL
        ) : FutureTarget<Bitmap> {
            return Glide.with(context)
                .asBitmap()
                .load(url)
                .submit(width,height)
        }
    }
}