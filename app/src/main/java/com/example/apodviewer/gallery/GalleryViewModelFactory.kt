package com.example.apodviewer.gallery

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apodviewer.database.PodDatabaseDao

class GalleryViewModelFactory (
    private val dataSource: PodDatabaseDao,
    private val application: Application) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
                return GalleryViewModel(dataSource, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }