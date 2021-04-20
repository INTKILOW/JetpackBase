package top.intkilow.jetpackbase.pages.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import top.intkilow.architecture.utils.LogUtil
import top.intkilow.avlib.AudioCore
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

//        AudioCore.instance.play("http://m701.music.126.net/20210419172053/bf2fe39b41dde05980d33b7b411c2582/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/7937682490/216e/250f/f5c5/aec3a3796126774fcdf7e2330181345c.mp3",
//        onDuration = {duration->
//            LogUtil.e(duration,"duration")
//        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        AudioCore.instance.release()
    }
}