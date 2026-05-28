package com.example.myapplication.network

import com.example.myapplication.model.PexelsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("curated")
    suspend fun getCuratedPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): PexelsResponse

    @GET("search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): PexelsResponse
}
