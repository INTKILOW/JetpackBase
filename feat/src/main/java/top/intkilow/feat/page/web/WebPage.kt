package top.intkilow.feat.page.web

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import top.intkilow.feat.databinding.FeatPageWebBinding

class WebPage : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding =
                FeatPageWebBinding.inflate(inflater, container, false)
       val url = arguments?.getString("url")
        binding.web.loadUrl(url)

        return binding.root
    }




}