package com.example.apodviewer.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Entity(tableName = "pod_items")
@Parcelize
data class PodItem(
    @PrimaryKey(autoGenerate = false)
    val dateMillis: Long,

    @ColumnInfo(name = "full_size_url")
    val fullSizeURL: String,

    @ColumnInfo(name = "thumbnail_url")
    val thumbnailURL: String,

    @ColumnInfo(name = "type")
    val type: PodTypes,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "copyright")
    val copyright: String? = null
): Parcelable{
    val isVideo
        get() = type == PodTypes.VIDEO

    val date
        get() = Date(dateMillis)
}

enum class PodTypes {
    IMAGE,
    VIDEO
}