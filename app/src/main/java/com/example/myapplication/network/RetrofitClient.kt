package com.example.myapplication.network

import android.content.Context
import com.example.myapplication.constants.ApiConstants
import com.example.myapplication.utils.HttpCacheNotifier
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var apiService: ApiService? = null

    fun getApiService(context: Context): ApiService {
        if (apiService == null) {
            synchronized(this) {
                if (apiService == null) {
                    apiService = createApiService(context)
                }
            }
        }
        return apiService!!
    }

    private fun createApiService(context: Context): ApiService {
        val cacheDir = File(context.cacheDir, "http_cache")
        val cache = Cache(cacheDir, ApiConstants.OKHTTP_CACHE_SIZE)

        val forceNetworkInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (request.header("X-Force-Network") == "true") {
                request = request.newBuilder()
                    .removeHeader("X-Force-Network")
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()
            }
            chain.proceed(request)
        }

        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", ApiConstants.API_KEY)
                .build()
            chain.proceed(request)
        }

        val cacheInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl = response.header("Cache-Control")
            if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache")) {
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=3600")
                    .build()
            } else {
                response
            }
        }

        val cacheHitInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val fromCache = response.cacheResponse != null && response.networkResponse == null
            HttpCacheNotifier.onResponse(fromCache)
            response
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(ApiConstants.OKHTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiConstants.OKHTTP_READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConstants.OKHTTP_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(forceNetworkInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(cacheHitInterceptor)
            .addNetworkInterceptor(cacheInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
