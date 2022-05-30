package com.example.apodviewer.network

import android.os.Parcelable
import com.example.apodviewer.database.PodItem
import com.example.apodviewer.database.PodTypes
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.parcel.Parcelize
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

private const val BASE_URL = "https://api.nasa.gov/"
private const val API_KEY = "dRZjoZiTlgh6qcUQuoT9pLvJLlJcxSQzzQi7PAlj"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface PodApiService {
    @GET("planetary/apod?api_key=$API_KEY&thumbs=true")
    suspend fun queryPods(
        @Query("start_date") start:String,
        @Query("end_date") end:String?
    ): List<PodRawItem>
}

object PodApi {
    private val retrofitService : PodApiService by lazy { retrofit.create(PodApiService::class.java) }

    suspend fun getPods(fromDate: String, toDate: String? = null): List<PodItem>{
        val rawPods = retrofitService.queryPods(fromDate, toDate)
        val pods: MutableList<PodItem> = mutableListOf()

        for (raw in rawPods) {
            val dateMillis = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(raw.date)!!.time
            if (!raw.thumbnail_url.isNullOrEmpty()){ // if definitely VIDEO
                pods.add(PodItem(
                    dateMillis = dateMillis,
                    fullSizeURL = raw.url,
                    thumbnailURL = raw.thumbnail_url,
                    type = PodTypes.VIDEO,
                    title = raw.title,
                    description = raw.description,
                    copyright = raw.copyright
                ))
            }else if(!raw.hdUrl.isNullOrEmpty()){ // if definitely IMAGE
                pods.add(PodItem(
                    dateMillis = dateMillis,
                    fullSizeURL = raw.hdUrl,
                    thumbnailURL = raw.url,
                    type = PodTypes.IMAGE,
                    title = raw.title,
                    description = raw.description,
                    copyright = raw.copyright
                ))
            }
        }
        pods.sortByDescending { it.dateMillis }
        return pods
    }

    suspend fun isTodayAvailable(): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            .format(Date())
        return try{
            retrofitService.queryPods(today, today)
            true
        }catch (e: Exception) {
            false
        }
    }
}

@Parcelize
data class PodRawItem(
    val date: String,
    @Json(name = "explanation") val description: String,
    @Json(name = "media_type") val type: String,
    val title: String,
    val thumbnail_url: String?,
    val url: String,
    @Json(name = "hdurl") val hdUrl: String?,
    val copyright: String?
): Parcelable