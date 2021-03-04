package top.intkilow.jetpackbase.pages.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import top.intkilow.architecture.nav.NavControllerHelper
import top.intkilow.architecture.utils.setOnClickDebounced
import top.intkilow.feat.constant.*
import top.intkilow.jetpackbase.R
import top.intkilow.jetpackbase.databinding.AppMainBinding

class MainPage : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AppMainBinding.inflate(inflater, container, false)
        binding.web.setOnClickDebounced {
            findNavController().navigate(
                R.id.springboard,
                Bundle().apply {
                    putString(
                        PAGE,
                        WEB_PAGE
                    )
                    putString("content","https://www.baidu.com")
                }, NavControllerHelper.getNavOptions()
            )
        }

        binding.scan.setOnClickDebounced {
            findNavController().navigate(
                R.id.springboard,
                Bundle().apply {
                    putString(
                        PAGE,
                        SCAN_PAGE
                    )
                }, NavControllerHelper.getNavOptions()
            )
        }


        binding.preview.setOnClickDebounced {

            val arr = ArrayList<String>()
            arr.add("https://t7.baidu.com/it/u=2168645659,3174029352&fm=193&f=GIF")
            findNavController().navigate(
                R.id.springboard,
                Bundle().apply {
                    putString(
                        PAGE,
                        PHOTO_PREVIEW_PAGE
                    )
                    putStringArrayList("images",arr)
                }, NavControllerHelper.getNavOptions()
            )
        }

        binding.choosePhoto.setOnClickDebounced {
            findNavController().navigate(
                R.id.springboard,
                Bundle().apply {
                    putString(
                        PAGE,
                        CHOOSE_PHOTO_PAGE
                    )
                }, NavControllerHelper.getNavOptions()
            )
        }

        return binding.root
    }
}