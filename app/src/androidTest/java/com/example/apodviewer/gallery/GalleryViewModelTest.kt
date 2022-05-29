package com.example.apodviewer.gallery

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.apodviewer.MainActivity
import com.example.apodviewer.R
import com.example.apodviewer.database.PodItem
import com.example.apodviewer.network.PodApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
class GalleryViewModelTest {

    @Before
    fun setUp() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun detailsCorrect() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        val fromDate = cal.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val items: List<PodItem>
        runBlocking {
            items = PodApi.getPods(dateFormat.format(fromDate))
        }

        for ((i, item) in items.withIndex()){
            if (!item.isVideo){
                onView(withId(R.id.gallery_list)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(i, click()))
                onView(withId(R.id.detailsTitle)).check(matches(withText(item.title)))
                return
            }
        }
        fail("No image items in selection")
    }
}