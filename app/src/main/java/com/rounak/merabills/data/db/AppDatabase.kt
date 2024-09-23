package com.rounak.merabills.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rounak.merabills.data.db.entities.Payment

@Database(entities = [Payment::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun appDao():AppDao
}