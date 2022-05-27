package com.example.apodviewer.details

import android.app.Application
import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.apodviewer.R
import com.example.apodviewer.database.PodItem

class DetailsViewModel(
    item: PodItem,
    application: Application
): ViewModel() {

    private val _podItem = MutableLiveData<PodItem>()
    val podItem: LiveData<PodItem>
        get() = _podItem

    init {
        _podItem.value = item
    }

    val displayCopyright = Transformations.map(podItem) { item ->
        item.copyright?.let {
            application.applicationContext.getString(R.string.details_copyright, item.copyright)
        }
    }

    val displayDate = Transformations.map(podItem) {
        val context = application.applicationContext
        val localFormat = DateFormat.getDateFormat(context)
        context.getString(R.string.details_date, localFormat.format(item.date))
    }
}