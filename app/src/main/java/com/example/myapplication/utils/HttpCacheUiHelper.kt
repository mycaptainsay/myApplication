package com.example.myapplication.utils

import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.DataSource
import com.example.myapplication.R

object HttpCacheUiHelper {

    fun showApiCacheHint(activity: AppCompatActivity) {
        showTopToast(activity, activity.getString(R.string.http_cache_hint_api))
    }

    fun showImageCacheHint(activity: AppCompatActivity, dataSource: DataSource) {
        val cacheType = when (dataSource) {
            DataSource.MEMORY_CACHE -> "内存缓存"
            DataSource.DATA_DISK_CACHE, DataSource.RESOURCE_DISK_CACHE -> "磁盘缓存"
            DataSource.LOCAL -> "本地资源"
            else -> "缓存"
        }
        showTopToast(activity, activity.getString(R.string.http_cache_hint_image, cacheType))
    }

    private fun showTopToast(activity: AppCompatActivity, message: String) {
        if (activity.isFinishing || activity.isDestroyed) return
        val toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 120)
        toast.show()
    }
}
