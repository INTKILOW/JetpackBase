package top.intkilow.jetpackbase.pages.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import top.intkilow.architecture.nav.NavControllerHelper
import top.intkilow.architecture.utils.FileTransfer
import top.intkilow.architecture.ui.SnackbarUtil
import top.intkilow.architecture.utils.LogUtil
import top.intkilow.architecture.utils.setOnClickDebounced
import top.intkilow.feat.constant.*
import top.intkilow.feat.page.photo.choose.CHOOSE_PHOTO_RESULT_DATA
import top.intkilow.feat.page.qr.SCAN_RESULT_DATA
import top.intkilow.feat.vo.PhotoVO
import top.intkilow.jetpackbase.R
import top.intkilow.jetpackbase.databinding.AppSettingBinding

class SettingPage : Fragment() {
    private val settingModel: SettingModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AppSettingBinding.inflate(inflater, container, false)

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
            settingModel.setDataStore()
        }
        binding.get.setOnClickDebounced {
            settingModel.getDataStore()
        }

        binding.downloadApk.setOnClickDebounced {
            binding.downloadApk.isEnabled = false
            // 地址随时失效，随便网上找的
            FileTransfer.download(
                url = "https://download.pc6.com/down/90694/",
                context = it.context,
                fileName = "a.apk",
                result = { file, msg ->
                    activity?.runOnUiThread {
                        binding.downloadApk.isEnabled = true
                        SnackbarUtil.toast(it,msg)
                        FileTransfer.installAPK(file, it.context)
                    }

                })
        }

        return binding.root
    }


}