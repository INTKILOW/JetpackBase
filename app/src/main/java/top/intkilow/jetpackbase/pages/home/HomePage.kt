package top.intkilow.jetpackbase.pages.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import top.intkilow.architecture.utils.LogUtil
import top.intkilow.jetpackbase.databinding.AppHomeBinding

class HomePage:Fragment() {
    private val homeModel by viewModels<HomeModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AppHomeBinding.inflate(inflater,container,false)

        binding.homeMode = homeModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}