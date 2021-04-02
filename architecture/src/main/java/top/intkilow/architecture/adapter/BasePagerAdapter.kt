package top.intkilow.architecture.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * 用于viewpager 首页底部tab 导航
   private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
       0 to { A() },
       1 to { B() },
       2 to { C() },
       3 to { D() },
   )
 */
class BasePagerAdapter(
    var tabFragmentsCreators: Map<Int, () -> Fragment>,
    fragment: Fragment
) : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: Fragment()
    }
}