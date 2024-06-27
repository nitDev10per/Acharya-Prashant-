package com.example.acharyaprashant

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaCoverageRepository(private val api: ApiService)  {
    suspend fun fetchMediaCoverages(limit: Int): List<MediaCoverage> {
        return withContext(Dispatchers.IO) {
            api.getMediaCoverages(limit)
        }
    }
}