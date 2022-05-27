package com.example.apodviewer.details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apodviewer.database.PodItem

class DetailsViewModelFactory(
    private val item: PodItem,
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(item, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}