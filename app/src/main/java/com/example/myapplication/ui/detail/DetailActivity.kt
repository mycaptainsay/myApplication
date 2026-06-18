package com.example.myapplication.ui.detail

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myapplication.constants.AppConstants
import com.example.myapplication.databinding.ActivityDetailBinding
import com.example.myapplication.model.Photo
import com.example.myapplication.utils.HttpCacheUiHelper

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var photo: Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        photo = intent.getParcelableExtra(AppConstants.INTENT_EXTRA_PHOTO)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        showPhoto()
    }

    private fun showPhoto() {
        photo?.let { item ->
            Glide.with(this)
                .load(item.src.large)
                .thumbnail(Glide.with(this).load(item.src.medium))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean = false

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (dataSource != DataSource.REMOTE) {
                            HttpCacheUiHelper.showImageCacheHint(this@DetailActivity, dataSource)
                        }
                        return false
                    }
                })
                .into(binding.ivDetailPhoto)

            binding.tvDetailPhotographer.text = item.photographer
            binding.tvDetailSize.text = "${item.width} x ${item.height}"
            binding.tvDetailAlt.text = item.alt ?: "Photo #${item.id}"
            binding.tvDetailUrl.text = item.src.original
            binding.tvPhotoId.text = "#${item.id}"

            val colorText = item.avgColor
            if (colorText != null) {
                try {
                    val color = Color.parseColor(colorText)
                    binding.vAvgColor.setBackgroundColor(color)
                    binding.tvAvgColor.text = colorText.uppercase()
                } catch (e: Exception) {
                    binding.tvAvgColor.text = "-"
                }
            } else {
                binding.tvAvgColor.text = "-"
            }
        }
    }
}
