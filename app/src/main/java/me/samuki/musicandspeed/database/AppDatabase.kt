package me.samuki.musicandspeed.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import me.samuki.musicandspeed.database.daos.IntervalDao
import me.samuki.musicandspeed.database.daos.ListDao
import me.samuki.musicandspeed.database.entities.IntervalEntity
import me.samuki.musicandspeed.database.entities.ListEntity

@Database(
        entities = [
        ListEntity::class,
        IntervalEntity::class
        ],
        version = 1,
        exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getListDao(): ListDao
    abstract fun getIntervalDao(): IntervalDao
}