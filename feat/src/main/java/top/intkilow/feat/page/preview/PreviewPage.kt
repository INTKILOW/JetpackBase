package top.intkilow.feat.page.preview

import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import top.intkilow.architecture.network.utils.FileTransfer
import top.intkilow.architecture.ui.SnackbarUtil
import top.intkilow.architecture.utils.ViewUtils
import top.intkilow.architecture.utils.setOnClickDebounced
import top.intkilow.feat.R
import top.intkilow.feat.databinding.FeaturePreviewPageBinding
import top.intkilow.feat.widget.photoview.PhotoView

/**
 * 图片预览
 * images:ArrayList<String>
 * current:int
 */
class PreviewPage : Fragment() {

    private val previewDialogModel by viewModels<PreviewModel>()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        val binding =
                FeaturePreviewPageBinding.inflate(inflater, container, false)
        binding.previewDialogModel = previewDialogModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewPager.addOnPageChangeListener(pageListener)
        arguments?.getStringArrayList("images")?.let {
            previewDialogModel.imgs.value = it
            binding.viewPager.adapter = SamplePagerAdapter(it)
            previewDialogModel.total.value = it.size
            arguments?.getInt("current", 0)?.let { currentIndex ->

                var index = 0
                if (currentIndex <= it.size - 1) {
                    index = currentIndex
                }
                binding.viewPager.currentItem = index

            }
        }

        context?.let {context->
            val layoutParams = binding.download.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin += ViewUtils.getStatusBarHeight(context)
            binding.download.layoutParams = layoutParams
        }


        binding.download.setOnClickDebounced {imageView->


            context?.let {context->
                val img = previewDialogModel.imgs.value?.get(binding.viewPager.currentItem)
                img?.let {

                    SnackbarUtil.run { toast(imageView,getString(R.string.feat_download_start)) }
                    FileTransfer.download(img,context,result = {file, msg ->
                        if(msg.contains("ok")){
                            SnackbarUtil.run { toast(imageView,getString(R.string.feat_download_success)) }
                        }
                    })
                }

            }

        }
        return binding.root
    }


    private val pageListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            previewDialogModel.current.value = (position + 1)

        }

        override fun onPageScrollStateChanged(state: Int) {
        }

    }


    inner class SamplePagerAdapter(val imgs: List<String>) : PagerAdapter() {

        override fun getCount(): Int {
            return imgs.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            Glide.with(container.context).load(imgs[position]).into(photoView)
            // Now just add PhotoView to ViewPager and return it
            container.addView(
                    photoView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
            photoView.setOnPhotoTapListener { _, _, _ ->
                val scale = photoView.attacher.scale
                if (scale != 1.0f) {
                    photoView.attacher.setScale(1.0f, true)
                } else {
                    findNavController().navigateUp()
                }
            }
            photoView.setOnLongClickListener {
                // 长按下载

                false
            }
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }


    }


}