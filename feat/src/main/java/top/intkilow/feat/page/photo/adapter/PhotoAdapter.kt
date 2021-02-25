package top.intkilow.feat.page.photo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import top.intkilow.architecture.ui.ToastUtil
import top.intkilow.feat.R
import top.intkilow.feat.databinding.FeatureCameraItemBinding
import top.intkilow.feat.databinding.FeaturePhotoItemBinding
import top.intkilow.feat.vo.PhotoVO
import top.intkilow.feat.widget.PhotoItem
import java.util.*

class PhotoAdapter(var data: LinkedList<PhotoVO>, val dataChange: (size: Int) -> Unit,
                   val preview: (p: Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class CameraHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var selectData = LinkedList<PhotoVO>()
    var selectSize = 9

    val CAMERA_TYPE = 1
    val PHOTO_TYPE = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == PHOTO_TYPE) {
            val binding = FeaturePhotoItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding.root)
        } else {
            val binding = FeatureCameraItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)

            CameraHolder(binding.root)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {


            val binding = DataBindingUtil
                    .getBinding<FeaturePhotoItemBinding>(holder.itemView)
            binding?.let {
                val vo = data[position]

                val image = it.image

                image.setImageClickCall(object : PhotoItem.ImageClickCall {
                    override fun onRectClick(enableClick: Boolean) {
                        if (enableClick) {
                            // 设置选中
                            val index = selectData.indexOf(vo)
                            if (index != -1) {
                                selectData.remove(vo)
                                notifyDataSetChanged()
                                dataChange(selectData.size)
                            } else {
                                if (selectData.size >= selectSize) {
                                    val format = image.context.resources.getString(R.string.feat_choose_photo_max)
                                    ToastUtil.toast(image, String.format(format, selectSize))
                                    return
                                }
                                selectData.add(vo)
                                notifyItemChanged(position)
                                dataChange(selectData.size)
                            }
                        }

                    }

                    override fun onImageClick() {
                        preview(position)
                    }

                })

                val indexOf = selectData.indexOf(vo)
                image.setSelect(indexOf != -1, indexOf + 1, false)

                Glide.with(holder.itemView.context).load(vo.uri).centerCrop().into(image)
                it.executePendingBindings()
            }
        } else {
            val binding = DataBindingUtil
                    .getBinding<FeatureCameraItemBinding>(holder.itemView)
            binding?.content?.setOnClickListener {
                preview(position)
            }
            binding?.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            CAMERA_TYPE
        } else {
            PHOTO_TYPE
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}