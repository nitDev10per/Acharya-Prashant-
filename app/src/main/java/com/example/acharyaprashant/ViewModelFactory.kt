package com.example.acharyaprashant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val repository: MediaCoverageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaCoverageViewModel::class.java)) {
            return MediaCoverageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}