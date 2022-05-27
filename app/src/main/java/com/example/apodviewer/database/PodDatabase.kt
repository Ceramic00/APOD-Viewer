package com.example.apodviewer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PodItem::class], version = 1, exportSchema = false)
abstract class PodDatabase : RoomDatabase() {
    abstract val podDatabaseDao: PodDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: PodDatabase? = null

        fun getInstance(context: Context): PodDatabase {
            synchronized(false) {
                var instance = INSTANCE

                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PodDatabase::class.java,
                        "pod_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}