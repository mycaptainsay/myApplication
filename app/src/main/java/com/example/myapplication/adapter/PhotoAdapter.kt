package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.constants.AppConstants
import com.example.myapplication.databinding.ItemPhotoBinding
import com.example.myapplication.model.Photo

class PhotoAdapter(
    private val onItemClick: (Photo, ImageView) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val photos = mutableListOf<Photo>()
    private var showLoading = false

    fun updateList(list: List<Photo>) {
        photos.clear()
        photos.addAll(list)
        notifyDataSetChanged()
    }

    fun addLoadingFooter() {
        if (!showLoading) {
            showLoading = true
            notifyItemInserted(photos.size)
        }
    }

    fun removeLoadingFooter() {
        if (showLoading) {
            showLoading = false
            notifyItemRemoved(photos.size)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (showLoading && position == photos.size) {
            AppConstants.VIEW_TYPE_LOADING
        } else {
            AppConstants.VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == AppConstants.VIEW_TYPE_LOADING) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading, parent, false)
            return LoadingViewHolder(view)
        }
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PhotoViewHolder) {
            holder.bind(photos[position])
        }
    }

    override fun getItemCount(): Int {
        return photos.size + if (showLoading) 1 else 0
    }

    class PhotoViewHolder(
        private val binding: ItemPhotoBinding,
        private val onItemClick: (Photo, ImageView) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            Glide.with(binding.ivPhoto.context)
                .load(photo.src.medium)
                .thumbnail(Glide.with(binding.ivPhoto.context).load(photo.src.small))
                .into(binding.ivPhoto)

            binding.tvPhotographer.text = photo.photographer
            binding.tvSize.text = "${photo.width} x ${photo.height}"
            binding.tvAlt.text = photo.alt ?: "Photo #${photo.id}"

            binding.root.setOnClickListener {
                onItemClick(photo, binding.ivPhoto)
            }
        }
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
