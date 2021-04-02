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
import top.intkilow.architecture.adapter.BasePagerAdapter
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
import top.intkilow.jetpackbase.pages.home.HomePage
import top.intkilow.jetpackbase.pages.setting.SettingPage

class MainPage : Fragment() {
    private val mainModel: MainModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AppMainBinding.inflate(inflater, container, false)


        val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
            0 to { HomePage() },
            1 to { SettingPage() },
        )
        // 禁止滑动
        binding.pager.isUserInputEnabled = false
        binding.pager.adapter = BasePagerAdapter(tabFragmentsCreators, this)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menu ->
            var index = 0
            when (menu.itemId) {
                R.id.home -> {

                    index = 0
                }
                R.id.setting -> {
                    index = 1
                }
            }
            mainModel.currentIndex.value = index
            true

        }
        mainModel.currentIndex.observe(viewLifecycleOwner) { index ->
            if (null != index) {
                binding.pager.setCurrentItem(index, false)
            }
        }

        return binding.root
    }


}