package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.myapplication.R
import com.example.myapplication.constants.AppConstants
import com.example.myapplication.databinding.ItemPhotoBinding
import com.example.myapplication.model.Photo

class PhotoAdapter(
    private val onItemClick: (Photo, ImageView) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoadingAdded = false

    private val diffCallback = object : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(photos: List<Photo>?) {
        differ.submitList(photos?.toList())
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoadingAdded && position == differ.currentList.size) {
            AppConstants.VIEW_TYPE_LOADING
        } else {
            AppConstants.VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == AppConstants.VIEW_TYPE_LOADING) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        } else {
            val binding = ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            PhotoViewHolder(binding, onItemClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PhotoViewHolder) {
            holder.bind(differ.currentList[position])
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size + if (isLoadingAdded) 1 else 0
    }

    inner class PhotoViewHolder(
        private val binding: ItemPhotoBinding,
        private val onItemClick: (Photo, ImageView) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            val aspectRatio = photo.width.toDouble() / photo.height.toDouble()

            Glide.with(binding.ivPhoto.context)
                .load(photo.src.medium)
                .thumbnail(
                    Glide.with(binding.ivPhoto.context)
                        .load(photo.src.small)
                )
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(binding.ivPhoto)

            binding.tvPhotographer.text = photo.photographer
            binding.tvSize.text = "${photo.width} × ${photo.height}"
            binding.tvAlt.text = photo.alt ?: "Photo #${photo.id}"

            binding.root.setOnClickListener {
                onItemClick(photo, binding.ivPhoto)
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
