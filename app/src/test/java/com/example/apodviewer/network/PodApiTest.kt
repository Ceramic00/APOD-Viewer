package com.example.apodviewer.network

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class PodApiTest {

    @Test
    fun `Get pods by date descending, correct order`() = runBlocking {
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        cal.set(Calendar.DATE, 1)
        val result = PodApi.getPods(dateFormat.format(cal.time))
        assert(result[0].dateMillis > result[1].dateMillis)
    }

    @Test
    fun `Get pods by wrong date, exception`() = runBlocking {
        val date = "000"
        try {
            val result = PodApi.getPods(date)
            fail("No exception. Result: " + result.toString())
        } catch (e: Exception) {
            return@runBlocking
        }
    }
}