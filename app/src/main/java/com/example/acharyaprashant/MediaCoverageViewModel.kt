package com.example.acharyaprashant

import ImageLoader
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MediaCoverageViewModel(private val repository: MediaCoverageRepository) : ViewModel() {

    private val _mediaCoverages = MutableLiveData<List<MediaCoverage>>()
    val mediaCoverages: LiveData<List<MediaCoverage>> get() = _mediaCoverages


    private var _bitMapData = MutableLiveData<Map<String,Bitmap>>()
    val bitMapData: LiveData<Map<String,Bitmap>>
        get() = _bitMapData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private var currentPage = 1

    fun getMediaCoverages(limit: Int) {
        viewModelScope.launch {
            try {
                val result = repository.fetchMediaCoverages(limit)
                _mediaCoverages.postValue(result)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    fun loadMoreMediaCoverages() {
        currentPage++
        getMediaCoverages(currentPage * 20)  // Adjust page size as needed
    }

    fun getMediaCache(context: Context) {
        viewModelScope.launch {
            val imageLoader = ImageLoader(context)
            val listBitmap = mutableMapOf<String, Bitmap>()

            // Check if mediaCoverages is not null and has elements
            val mediaList = mediaCoverages.value ?: return@launch

            withContext(Dispatchers.IO) {
                if (mediaList.isNotEmpty()) {
                    for (image in mediaList) {
                        val imageUrl = "${image.thumbnail.domain}/${image.thumbnail.basePath}/0/${image.thumbnail.key}"
                        val bitmap = imageLoader.getImage(imageUrl)
                        bitmap?.let { listBitmap[imageUrl] = it }
                    }
                }
            }

            _bitMapData.postValue(listBitmap)
        }
    }


}
