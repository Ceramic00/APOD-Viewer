package com.example.apodviewer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import androidx.room.Update

@Dao
interface PodDatabaseDao {
    @Insert(onConflict = IGNORE)
    fun insertMultiple(items: List<PodItem>)

    @Update
    fun update(item: PodItem)

    @Query("SELECT * FROM pod_items WHERE dateMillis = :dateMillis")
    fun getItem(dateMillis: Long): PodItem?

    @Query("SELECT * FROM pod_items WHERE dateMillis BETWEEN :fromDateMillis AND :toDateMillis ORDER BY dateMillis DESC")
    fun getItemsBetween(fromDateMillis: Long, toDateMillis: Long): List<PodItem>?
}