package me.samuki.musicandspeed.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import me.samuki.musicandspeed.database.daos.ListDao
import me.samuki.musicandspeed.database.entities.ListEntity

@Database(entities = [
    ListEntity::class
], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun listDao(): ListDao
}