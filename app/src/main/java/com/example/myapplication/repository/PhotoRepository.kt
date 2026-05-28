package com.example.myapplication.repository

import android.content.Context
import com.example.myapplication.constants.ApiConstants
import com.example.myapplication.model.PexelsResponse
import com.example.myapplication.model.Result
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotoRepository(private val context: Context) {
    private val apiService = RetrofitClient.getApiService(context)

    suspend fun getCuratedPhotos(page: Int): Result<PexelsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCuratedPhotos(page, ApiConstants.DEFAULT_PER_PAGE)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Unknown error", e)
            }
        }
    }

    suspend fun searchPhotos(query: String, page: Int): Result<PexelsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchPhotos(query, page, ApiConstants.DEFAULT_PER_PAGE)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Unknown error", e)
            }
        }
    }
}
