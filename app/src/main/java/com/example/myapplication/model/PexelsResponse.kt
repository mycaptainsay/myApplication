package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class PexelsResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("photos") val photos: List<Photo>,
    @SerializedName("total_results") val totalResults: Int
)
