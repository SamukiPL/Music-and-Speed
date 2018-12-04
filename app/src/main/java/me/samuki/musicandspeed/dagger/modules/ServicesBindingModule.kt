package me.samuki.musicandspeed.dagger.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.samuki.musicandspeed.dagger.scopes.ServiceScope
import me.samuki.musicandspeed.services.media.CustomBrowserService

@Module
abstract class ServicesBindingModule {

    @ContributesAndroidInjector
    @ServiceScope
    abstract fun provideCustomBrowserService(): CustomBrowserService

}