package me.samuki.musicandspeed.dagger.modules

import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import me.samuki.musicandspeed.dagger.scopes.AppScope
import me.samuki.musicandspeed.services.media.MusicLibrary

@Module
class UtilitiesModule {

    @AppScope
    @Provides
    fun provideMusicLibrary(contentResolver: ContentResolver) = MusicLibrary(contentResolver)

}