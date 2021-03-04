package top.intkilow.architecture.paging

import androidx.recyclerview.widget.DiffUtil

class PagingDiff {

    companion object{
        fun <T> getDefaultDiff():DiffUtil.ItemCallback<T>{
            return object :DiffUtil.ItemCallback<T>(){
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }
}