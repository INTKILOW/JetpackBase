package top.intkilow.architecture.datasource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import top.intkilow.architecture.R
import top.intkilow.architecture.databinding.PagingFooterBinding
import top.intkilow.architecture.ui.ViewHolder

/**
 * 加载底部封装
 */
class PagingLoadStateAdapter<T : Any, VH : RecyclerView.ViewHolder>
(val adapter: PagingDataAdapter<T, VH>) : LoadStateAdapter<ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        val binding = DataBindingUtil.getBinding<PagingFooterBinding>(holder.itemView)

        binding?.let {


            when (loadState) {
                is LoadState.Error -> {
                    binding.loading.visibility = View.GONE
                    binding.loadingMsg.visibility = View.VISIBLE
                    binding.loadingMsg.text = it.root.context.getString(R.string.architecture_loading_fail)
                    binding.loadingMsg.setOnClickListener {
                        adapter.retry()
                    }
                }
                is LoadState.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                    binding.loadingMsg.visibility = View.VISIBLE
                    binding.loadingMsg.text = it.root.context.getString(R.string.architecture_loading)
                }
                is LoadState.NotLoading -> {
                    binding.loading.visibility = View.GONE
                    binding.loadingMsg.visibility = View.GONE
                }
            }
            it.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val binding = PagingFooterBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }


}