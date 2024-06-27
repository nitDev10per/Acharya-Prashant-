package com.example.acharyaprashant

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("content/misc/media-coverages")
    suspend fun getMediaCoverages(@Query("limit") limit: Int): List<MediaCoverage>
}