package top.intkilow.feat.page.web

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import top.intkilow.architecture.interfaces.IOnBackPressed
import top.intkilow.architecture.utils.ViewUtils
import top.intkilow.feat.R
import top.intkilow.feat.databinding.FeatPageWebBinding
import top.intkilow.feat.widget.TBSWebView
import java.util.*

class WebPage : Fragment() {


    private lateinit var binding: FeatPageWebBinding
    private var mTitle: String? = null

    private val mTBSCallback = object : TBSWebView.TBSWebViewCallback {
        override fun onPageStarted() {

        }

        override fun onPageFinished() {
        }

        override fun collapsing(collapsing: Boolean, collapsingLayout: LinearLayout?) {

        }

        override fun onImageClick(p: Int, imgs: LinkedList<String>, rect: Rect) {

        }

        override fun onReceivedTitle(title: String?) {
            var t = ""
            mTitle?.let { m ->
                t = m
            } ?: run {
                title?.let {
                    t = title
                }
            }
            binding.toolbar.title = t
        }

        override fun onReceivedError(e: Exception?) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FeatPageWebBinding.inflate(inflater, container, false)
        val content = arguments?.getString("content") ?: ""
        val contentType = arguments?.getString("type") ?: "url"

        context?.let { context ->
            binding.topView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewUtils.getStatusBarHeight(context)
            )
        }
        binding.toolbar.setNavigationOnClickListener {
            if (binding.web.canGoBack()) {
                binding.web.goBack()
            } else {
                findNavController().popBackStack()
            }
        }
        binding.web
            .setTBSWebViewCallback(mTBSCallback)
            .enableProgress()


        if (contentType == "url") {
            binding.web.loadUrl(content)
        } else {
            binding.web.loadRichText(content)
        }


        return binding.root
    }


}