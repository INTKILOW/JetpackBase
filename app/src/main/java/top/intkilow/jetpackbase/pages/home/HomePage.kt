package top.intkilow.jetpackbase.pages.home

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import top.intkilow.architecture.ui.ViewHolder
import top.intkilow.architecture.utils.LogUtil
import top.intkilow.architecture.utils.ViewUtils
import top.intkilow.jetpackbase.R
import top.intkilow.jetpackbase.databinding.AppHomeBinding

class HomePage : Fragment() {
    private val homeModel by viewModels<HomeModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AppHomeBinding.inflate(inflater, container, false)

        binding.homeMode = homeModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.emojiRecyclerView.adapter = EmojiAdapter()

        binding.emojiRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                LogUtil.e(dy)
            }

        })


        return binding.root
    }

    inner class EmojiAdapter : RecyclerView.Adapter<ViewHolder>() {
        var spaceCount: Int = 8
        val size = 70
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val root =
                LayoutInflater.from(parent.context).inflate(R.layout.emoji_item, parent, false)

            return ViewHolder(root)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val imageView = holder.itemView.findViewById<ImageView>(R.id.emoji_image)
            imageView.setBackgroundResource(R.drawable.eyt)
            val row: Int = position / spaceCount
            val col: Int = position % spaceCount

            if (spaceCount - col <= 2 && row > 3) {
                imageView.alpha = 0.1f
            } else {
                imageView.alpha = 1f
            }

            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams

            // 最后一行添加 margin
            val margin = if (position >= size - (size % spaceCount)) {
                ViewUtils.dp2px(imageView.context, 60f)
            } else {
                0
            }
            params.bottomMargin = margin
            holder.itemView.layoutParams = params
        }

        override fun getItemCount(): Int {
            return size
        }

    }


}