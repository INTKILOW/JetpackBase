package top.intkilow.jetpackbase.pages.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import top.intkilow.architecture.nav.NavControllerHelper
import top.intkilow.architecture.network.NetWorkManager
import top.intkilow.architecture.utils.DataStoreUtil
import top.intkilow.architecture.utils.LogUtil
import top.intkilow.architecture.utils.setOnClickDebounced
import top.intkilow.feat.constant.*
import top.intkilow.feat.page.photo.choose.CHOOSE_PHOTO_RESULT_DATA
import top.intkilow.feat.page.qr.SCAN_RESULT_DATA
import top.intkilow.feat.vo.PhotoVO
import top.intkilow.jetpackbase.R
import top.intkilow.jetpackbase.databinding.AppMainBinding

class MainPage : Fragment() {
    private val  mainModel: MainModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AppMainBinding.inflate(inflater, container, false)


        NavControllerHelper.getSavedStateHandle<List<PhotoVO>>(
            this,
            CHOOSE_PHOTO_RESULT_DATA,
            result = {

                LogUtil.e(it, "66666")
            },
            viewLifecycleOwner
        )
        NavControllerHelper.getSavedStateHandle<String>(
            this,
            SCAN_RESULT_DATA,
            result = {
                LogUtil.e(it, "66666")
            },
            viewLifecycleOwner
        )


        binding.web.setOnClickDebounced {
            findNavController().navigate(
                R.id.springboard,
                Bundle().apply {
                    putString(
                        PAGE,
                        WEB_PAGE
                    )
                    putString("content", "https://www.baidu.com")
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
            arr.add("https://t7.baidu.com/it/u=2168645659,3174029352&fm=193&f=GIF")
            findNavController().navigate(
                R.id.springboard,
                Bundle().apply {
                    putString(
                        PAGE,
                        PHOTO_PREVIEW_PAGE
                    )
                    putStringArrayList("images", arr)
                    putInt("current", 1)
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
                    putInt("max", 4)
                    putInt("destinationId", 0)
                }, NavControllerHelper.getNavOptions()
            )
        }
        binding.set.setOnClickDebounced {
            mainModel.setDataStore()
        }
        binding.get.setOnClickDebounced {
            mainModel.getDataStore()
        }

        LogUtil.block {
            LogUtil.e("0000000000000000")
            LogUtil.e("111111")
            LogUtil.e("22222")
            LogUtil.e("333")
        }

        return binding.root
    }



}