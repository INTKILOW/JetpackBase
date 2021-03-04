package top.intkilow.architecture.nav

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import top.intkilow.architecture.R
import java.util.*


class NavControllerHelper {

    companion object {
        fun fixNavActionBar(fragment: Fragment?) {
            fragment?.let { f ->
                val activity = f.activity
                if (activity is AppCompatActivity) {
                    NavigationUI.setupActionBarWithNavController(activity, findNavController(f))
                }
            }
        }

        fun navMainDestination(fragment: Fragment, startDestination: Int, args: Bundle) {
            navMainDestination(fragment, startDestination, true, args)
        }

        fun navMainDestination(fragment: Fragment, startDestination: Int) {
            navMainDestination(fragment, startDestination, Bundle())
        }

        fun navMainDestination(
            fragment: Fragment,
            startDestination: Int,
            inclusive: Boolean,
            args: Bundle
        ) {
            val controller = findNavController(fragment)
            val graph = controller.graph
            graph.startDestination = startDestination
            controller.graph = graph
            // NavigationUI.setupActionBarWithNavController(fragment, findNavController(fragment))
            /**
             * setPopUpTo(int destinationId, boolean inclusive)-在导航之前弹出到给定的目的地。
             * 这将从后堆栈中弹出所有不匹配的目标，直到找到该目标为止。
             * destinationId -弹出目的地，清除所有中间目的地。
             * inclusive -true还可以从后堆栈中弹出给定的目标。
             */
            val navOptions = getNavOptions(startDestination, inclusive)

            findNavController(fragment).navigate(startDestination, args, navOptions)
        }

        fun safeNav(call: () -> Unit) {
            try {
                call.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun addGraph(navController: NavController, graphName: String, context: Context) {

            try {
                val navId = context.resources.getIdentifier(
                    graphName,
                    "navigation",
                    context.packageName
                )
                val newGraph = navController.navInflater.inflate(navId)
                navController.graph.addAll(newGraph)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


        fun getNavOptions(destinationId: Int = -1, inclusive: Boolean = true): NavOptions {

            val builder = NavOptions.Builder().apply {
                setEnterAnim(R.anim.base_page_in)
                setExitAnim(R.anim.base_page_out)
                setPopEnterAnim(R.anim.base_page_pop_in)
                setPopExitAnim(R.anim.base_page_pop_out)
            }
            if (destinationId != -1) {
                /**
                 * destinationId ：弹出的目的地，清除所有中间目的地。
                 * inclusive ：true也会从后堆栈中弹出给定的目标。
                 */
                builder.setPopUpTo(destinationId, inclusive)
            }

            return builder.build()
        }

        /**
         * 设置回调
         * 类似 setResult
         */
        fun <T> setSavedStateHandle(
            fragment: Fragment,
            key: String,
            data: T,
            destinationId: Int = -1
        ) {

            val savedStateHandle: SavedStateHandle? = if (destinationId > 0) {
                try {
                    findNavController(fragment).getBackStackEntry(destinationId).savedStateHandle
                } catch (e: Exception) {
                    findNavController(fragment).previousBackStackEntry?.savedStateHandle
                }
            } else {
                findNavController(fragment).previousBackStackEntry?.savedStateHandle
            }

            savedStateHandle?.apply {
                getLiveData<T>(key).apply {
                    value = data
                }
            }
        }

        /**
         * 获取回调
         * 类似 startForResult
         */
        fun <T> getSavedStateHandle(
            fragment: Fragment,
            key: String,
            result: (data: T) -> Unit,
            lifecycleOwner: LifecycleOwner
        ) {
            findNavController(fragment)
                .currentBackStackEntry
                ?.savedStateHandle
                ?.let { savedStateHandle ->
                    savedStateHandle.getLiveData<T>(key).observe(lifecycleOwner) { data ->
                        savedStateHandle.remove<T>(key)
                        result(data)
                    }
                }
        }


    }

}