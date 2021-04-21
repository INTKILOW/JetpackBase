package top.intkilow.jetpackbase.pages.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import top.intkilow.architecture.ui.ViewHolder
import top.intkilow.architecture.utils.LogUtil
import top.intkilow.architecture.utils.ViewUtils
import top.intkilow.jetpackbase.R
import top.intkilow.jetpackbase.databinding.AppHomeBinding


class HomePage : Fragment() {
    var offset = 0
    var emojiHeight = 0
    var emojiTop = 0
    var actionTop = 0
    private val homeModel by viewModels<HomeModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AppHomeBinding.inflate(inflater, container, false)

        binding.homeMode = homeModel
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = EmojiAdapter(binding.emojiRecyclerView.context)


        binding.emojiRecyclerView.adapter = adapter
        (binding.emojiRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations =
            false

        binding.emojiRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
//                Log.e("onScrolled","TAG")

                recyclerView.post {
                    updateUI(recyclerView, binding.action, dy, adapter)
                }


            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // OnScrollListener.SCROLL_STATE_FLING; //屏幕处于甩动状态
                // OnScrollListener.SCROLL_STATE_IDLE; //停止滑动状态
                // OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;// 手指接触状态
                // 记录当前滑动状态
                LogUtil.e(newState, "newState")
                if (newState == SCROLL_STATE_IDLE) {
                    recyclerView.post {

                        if (recyclerView.computeVerticalScrollOffset() == 0) {
                            offset = 0
                        }

                        recyclerView.post {

                            updateUI(recyclerView, binding.action, 0, adapter)
                        }
                    }
                }
            }

        })


//        updateUI(binding.emojiRecyclerView, binding.action, 0, adapter)

        return binding.root
    }

    fun updateUI(recyclerView: RecyclerView, action: View, dy: Int, adapter: EmojiAdapter) {
        offset += dy
        if (emojiHeight <= 0 || emojiTop <= 0 || actionTop <= 0) {
            // item 高度
            emojiHeight = recyclerView.layoutManager?.getChildAt(0)?.height ?: 0
            // emoji距离上边距离
            emojiTop = recyclerView.top
            // 操作按钮距离上边距离
            actionTop = action.top
        }
        val cH = actionTop - emojiTop
        // 获取基本隐藏
        val baseRow = cH / emojiHeight
        val oH = offset + (cH % emojiHeight)
        val offsetRow = (oH) / emojiHeight
        // 当前item 划过的距离
        val offsetRowPercent = (oH) % emojiHeight
        // 仿微信实现item 1/2高度开始计算透明度
        val bH = emojiHeight / 2
        val percent = if (offsetRowPercent < bH) {
            0f
        } else {
            (offsetRowPercent - bH) / (bH * 1.0f)
        }

        adapter.setNewRow(baseRow + offsetRow, percent, dy)
    }

    inner class EmojiAdapter(context: Context) : RecyclerView.Adapter<ViewHolder>() {
        var spaceCount: Int = 8
        var currentLine = 0
        var currentLinePercent = 1.0f
        val iamges: List<Int> = getImageRes(context)
        val size = iamges.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val root =
                LayoutInflater.from(parent.context).inflate(R.layout.emoji_item, parent, false)

            return ViewHolder(root)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val imageView = holder.itemView.findViewById<ImageView>(R.id.emoji_image)
            imageView.setBackgroundResource(iamges[position])


//            Glide.with(imageView.context).load(iamges[position]).into(imageView)
            val row: Int = position / spaceCount
            val col: Int = position % spaceCount


            val alpha = if (spaceCount - col <= 2 && row >= currentLine) {
                if (row == currentLine) {
                    currentLinePercent
                } else {
                    0.0f
                }

            } else {
                1f
            }
            imageView.alpha = alpha

            // 点击效果
            holder.itemView.findViewById<ConstraintLayout>(R.id.root).isClickable = alpha >= 1f


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

        fun setNewRow(newRow: Int, percent: Float, dy: Int) {
            currentLine = newRow
            currentLinePercent = percent
            if (dy == 0) {
                // 循环设置图标
                for (index in 1..size / spaceCount) {
                    notifyItemRangeChanged((index) * spaceCount + (spaceCount - 2), 2)
                }
                return
            }
            notifyItemRangeChanged((currentLine) * spaceCount + (spaceCount - 2), 2)


        }

    }

    fun getImageRes(context: Context): List<Int> {
        val imgList: ArrayList<Int> = ArrayList()
        for (index in 1..95) {
            val imgName = "emoji_$index"
            val imgId = resources.getIdentifier(imgName, "drawable", context.packageName)
            imgList.add(imgId)
        }

        return imgList
    }


}