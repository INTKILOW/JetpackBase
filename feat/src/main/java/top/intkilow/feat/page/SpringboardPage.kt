package top.intkilow.feat.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import top.intkilow.architecture.nav.NavControllerHelper
import top.intkilow.feat.R
import top.intkilow.feat.constant.CHOOSE_PHOTO_PAGE
import top.intkilow.feat.constant.PAGE
import top.intkilow.feat.constant.PHOTO_PREVIEW_PAGE
import top.intkilow.feat.constant.SCAN_PAGE

/**
 * 跳板页面
 *
 *
  findNavController().navigate(
    R.id.springboard,
    Bundle().apply {
    putString(
    PAGE,
    WEB_PAGE
    )
    }, NavControllerHelper.getNavOptions()
    )
 */


class SpringboardPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val page = arguments?.getString(PAGE, "") ?: ""
        var resId = R.id.webPage
        when (page) {
            CHOOSE_PHOTO_PAGE -> {
                resId = R.id.choosePhoto
            }
            PHOTO_PREVIEW_PAGE->{
                resId = R.id.previewPage
            }
            SCAN_PAGE->{
                resId = R.id.QRScanPage
            }
        }
        findNavController().navigate(resId,
                arguments,
                NavControllerHelper.getNavOptions(R.id.springboardPage,true)
            )



        return super.onCreateView(inflater, container, savedInstanceState)
    }
}