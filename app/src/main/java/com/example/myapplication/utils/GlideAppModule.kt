package com.example.myapplication.utils

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import com.example.myapplication.constants.ApiConstants

@GlideModule
class GlideAppModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val memoryCacheSizeBytes = ApiConstants.GLIDE_MEMORY_CACHE_SIZE_MB * 1024 * 1024
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes.toLong()))

        val diskCacheSizeBytes = ApiConstants.GLIDE_DISK_CACHE_SIZE_MB * 1024 * 1024
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, "glide_cache", diskCacheSizeBytes.toLong()))

        super.applyOptions(context, builder)
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}
