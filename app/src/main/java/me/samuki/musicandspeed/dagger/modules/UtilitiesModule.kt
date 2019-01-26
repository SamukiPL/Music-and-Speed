package me.samuki.musicandspeed.dagger.modules

import android.app.Application
import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import me.samuki.musicandspeed.dagger.scopes.AppScope
import me.samuki.musicandspeed.services.media.MusicLibrary
import me.samuki.musicandspeed.utility.AppPreferences

@Module
class UtilitiesModule {

    @AppScope
    @Provides
    fun provideMusicLibrary(contentResolver: ContentResolver) = MusicLibrary(contentResolver)

    @AppScope
    @Provides
    fun provideAppPreferences(application: Application) = AppPreferences(application)

}