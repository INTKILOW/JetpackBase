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
            findNavController().navigate(
                R.id.springboard,
                Bundle().apply {
                    putString(
                        PAGE,
                        WEB_PAGE
                    )
                    putString("url","https://www.baidu.com")
                }, NavControllerHelper.getNavOptions()
            )


        }

        return binding.root
    }
}