package com.example.myapplication.repository

import android.content.Context
import com.example.myapplication.constants.ApiConstants
import com.example.myapplication.model.PexelsResponse
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotoRepository(context: Context) {

    private val apiService = RetrofitClient.getApiService(context)

    suspend fun getCuratedPhotos(page: Int, forceNetwork: Boolean = false): PexelsResponse {
        return withContext(Dispatchers.IO) {
            apiService.getCuratedPhotos(
                page,
                ApiConstants.DEFAULT_PER_PAGE,
                if (forceNetwork) "true" else null
            )
        }
    }

    suspend fun searchPhotos(query: String, page: Int, forceNetwork: Boolean = false): PexelsResponse {
        return withContext(Dispatchers.IO) {
            apiService.searchPhotos(
                query,
                page,
                ApiConstants.DEFAULT_PER_PAGE,
                if (forceNetwork) "true" else null
            )
        }
    }
}
