package top.intkilow.architecture.adapter
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import top.intkilow.architecture.utils.DisplayUtil


@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("bindSrc")
fun bindSrc(view: ImageView, resId: Int) {
    Glide.with(view.context).load(resId)
            .centerCrop()
            .into(view)
}

@BindingAdapter(value = ["imageFromUrl", "corner"], requireAll = false)
fun bindImageFromUrl(view: ImageView, imageUrl: String?, corner: Int?) {
    if (!imageUrl.isNullOrEmpty()) {
        var transition = Glide.with(view.context)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())

        var options = RequestOptions()
                .centerCrop()

        corner?.let { c ->
            // corner 是dp 需要转 px
            options = options.transform(CenterCrop(),
                    RoundedCorners(DisplayUtil.dp2px(view.context, c.toFloat())))
        }
        transition = transition.apply(options)
        transition.into(view)
    }
}


