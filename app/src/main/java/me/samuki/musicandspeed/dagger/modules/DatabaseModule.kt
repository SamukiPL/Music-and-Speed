package me.samuki.musicandspeed.dagger.modules

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import me.samuki.musicandspeed.dagger.scopes.AppScope
import me.samuki.musicandspeed.database.AppDatabase

private const val DATABASE_NAME = "initial_music.db"

@Module
class DatabaseModule {

    @AppScope
    @Provides
    fun provideAppDatabase(app: Application) = Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .allowMainThreadQueries()
            .build()

}