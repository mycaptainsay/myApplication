package com.example.myapplication.ui.detail

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.myapplication.R
import com.example.myapplication.constants.AppConstants
import com.example.myapplication.databinding.ActivityDetailBinding
import com.example.myapplication.model.Photo

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var photo: Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        photo = intent.getParcelableExtra(AppConstants.INTENT_EXTRA_PHOTO)

        initListeners()
        bindPhotoData()
    }

    private fun initListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun bindPhotoData() {
        photo?.let {
            Glide.with(this)
                .load(it.src.large)
                .thumbnail(
                    Glide.with(this)
                        .load(it.src.medium)
                )
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .into(binding.ivDetailPhoto)

            binding.tvDetailPhotographer.text = it.photographer
            binding.tvDetailSize.text = "${it.width} × ${it.height}"
            binding.tvDetailAlt.text = it.alt ?: "Photo #${it.id}"
            binding.tvDetailUrl.text = it.src.original
            binding.tvPhotoId.text = "#${it.id}"

            it.avgColor?.let { colorString ->
                try {
                    val color = Color.parseColor(colorString)
                    binding.vAvgColor.setBackgroundColor(color)
                    binding.tvAvgColor.text = colorString.uppercase()
                } catch (e: Exception) {
                    binding.tvAvgColor.text = "-"
                }
            } ?: run {
                binding.tvAvgColor.text = "-"
            }
        }
    }
}
