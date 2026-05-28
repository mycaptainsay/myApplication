package com.example.myapplication.constants

import com.example.myapplication.BuildConfig

object ApiConstants {
    const val BASE_URL = "https://api.pexels.com/v1/"
    val API_KEY: String get() = BuildConfig.PEXELS_API_KEY
    const val DEFAULT_PER_PAGE = 20
    const val FIRST_PAGE = 1

    const val OKHTTP_CACHE_SIZE = 50L * 1024 * 1024
    const val OKHTTP_CONNECT_TIMEOUT = 30L
    const val OKHTTP_READ_TIMEOUT = 30L
    const val OKHTTP_WRITE_TIMEOUT = 30L

    const val GLIDE_MEMORY_CACHE_SIZE_MB = 40
    const val GLIDE_DISK_CACHE_SIZE_MB = 250
}
