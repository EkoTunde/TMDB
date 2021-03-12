package com.ekosoftware.tmdb.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ekosoftware.tmdb.data.model.MovieEntity

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}