package com.example.acharyaprashant
data class MediaCoverageResponse(val list: MutableList<MediaCoverage>)
data class MediaCoverage(
    val id: String,
    val title: String,
    val language: String,
    val thumbnail: Thumbnail
)

data class Thumbnail(
    val id: String,
    val version: Int,
    val domain: String,
    val basePath: String,
    val key: String,
    val qualities: List<Int>,
    val aspectRatio: Float
)