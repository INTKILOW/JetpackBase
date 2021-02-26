package top.intkilow.jetpackbase.pages.main

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import top.intkilow.architecture.nav.NavControllerHelper
import top.intkilow.architecture.network.utils.FileTransfer
import top.intkilow.architecture.utils.ViewUtils
import top.intkilow.architecture.utils.setOnClickDebounced
import top.intkilow.feat.constant.CHOOSE_PHOTO_PAGE
import top.intkilow.feat.constant.PAGE
import top.intkilow.feat.constant.SCAN_PAGE
import top.intkilow.feat.constant.WEB_PAGE
import top.intkilow.jetpackbase.R
import top.intkilow.jetpackbase.databinding.AppMainBinding

class MainPage : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AppMainBinding.inflate(inflater, container, false)
        binding.scan.setOnClickDebounced {
//            findNavController().navigate(
//                R.id.springboard,
//                Bundle().apply {
//                    putString(
//                        PAGE,
//                        SCAN_PAGE
//                    )
//                }, NavControllerHelper.getNavOptions()
//            )
            val url =
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.deskcity.org%2Fpic_source%2Fe2%2Fa6%2F2e%2Fe2a62ec3aad5780c0f788431a12782e9.jpg&refer=http%3A%2F%2Fup.deskcity.org&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1616916059&t=fc2775f14f56966614e5fda259960a3e"


            context?.let {

                lifecycleScope.launch {
                    FileTransfer.download(
                        url,
                        it,
                        "test.png",
                        result = { file, msg ->
                            Log.e("TAG", "result = $msg")
                        },
                        progressCall = {
                            Log.e("TAG", "porgress = $it")

                        },
                        false
                    )
                }


            }

        }
        context?.let {
            ViewUtils.getGradientDrawable(
                it, 0xFFFFFF, GradientDrawable.RECTANGLE, 0f,
                floatArrayOf(2f, 2f, 2f)
            )
        }
        return binding.root
    }
}