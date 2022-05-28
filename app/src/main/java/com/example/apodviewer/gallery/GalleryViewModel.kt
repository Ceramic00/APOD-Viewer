package com.example.apodviewer.gallery

import androidx.lifecycle.AndroidViewModel
import android.app.Application
import android.text.format.DateUtils.isToday
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.apodviewer.database.PodDatabaseDao
import com.example.apodviewer.database.PodItem
import com.example.apodviewer.network.PodApi
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

enum class PodApiStatus {LOADING, DONE, ERROR, FATAL}

class GalleryViewModel(
    val database: PodDatabaseDao,
    application: Application): AndroidViewModel(application) {

    private val _status = MutableLiveData<PodApiStatus>()
    val status: LiveData<PodApiStatus>
        get() = _status

    private val _podItems = MutableLiveData<List<PodItem>>()
    val podItems: LiveData<List<PodItem>>
        get() = _podItems

    private val _navigateToDetails = MutableLiveData<PodItem?>()
    val navigateToDetails: LiveData<PodItem?>
        get() = _navigateToDetails

    val showNetworkError = MutableLiveData<Boolean>()

    private var nextPodItemDate: Date? = null

    init {
        getNextPodItems()
    }

    fun getNextPodItems() {
        viewModelScope.launch {
            val previousStatus = _status.value
            _status.value = PodApiStatus.LOADING

            // Creating dates from first date of month to last/today
            val cal = Calendar.getInstance()
            val toDate = nextPodItemDate ?: Date(System.currentTimeMillis())
            cal.time = toDate
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val fromDate = cal.time
            cal.add(Calendar.DATE, -1)
            nextPodItemDate = cal.time

            val currentItems = _podItems.value ?: ArrayList()
            val cache = getValidCache(fromDate, toDate)
            if (cache.isNullOrEmpty()) {
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")

                    // If first request - send only start date
                    // in case if tomorrow image not ready yet
                    val result =
                        if (currentItems.isEmpty())
                            PodApi.getPods(dateFormat.format(fromDate))
                        else
                            PodApi.getPods(dateFormat.format(fromDate), dateFormat.format(toDate))

                    _podItems.value = currentItems + result
                    _status.value = PodApiStatus.DONE

                    insertPodItemsToDatabase(result)
                } catch (e: Exception) {
                    Log.e("No Internet or NASA API error", e.toString())

                    // Show error screen only if nothing else to show
                    _status.value =
                        if (currentItems.isEmpty())
                            PodApiStatus.FATAL
                        else
                            PodApiStatus.ERROR

                    // If previous request succeeded - show error message
                    if (previousStatus != PodApiStatus.ERROR)
                        showNetworkError.value = true
                }
            } else {
                _podItems.value = currentItems + cache
                _status.value = PodApiStatus.DONE
            }
        }
    }

    private suspend fun getValidCache(fromDate: Date, toDate: Date): List<PodItem>? {
        return withContext(Dispatchers.IO) {
            val cache = database.getItemsBetween(fromDate.time, toDate.time)

            if (cache.isNullOrEmpty()
                || (isToday(toDate.time) && PodApi.isTodayAvailable() && !isToday(cache[0].dateMillis)))
                // Updating cache if new NASA image already in stock
                null
            else
                cache
        }
    }

    private suspend fun insertPodItemsToDatabase(items: List<PodItem>) {
        withContext(Dispatchers.IO) {
            database.insertMultiple(items)
        }
    }

    fun displayDetails(item: PodItem) {
        _navigateToDetails.value = item
    }

    fun displayDetailsComplete() {
        _navigateToDetails.value = null
    }
}