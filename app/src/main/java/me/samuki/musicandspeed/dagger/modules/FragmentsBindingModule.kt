package me.samuki.musicandspeed.dagger.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.samuki.musicandspeed.activities.main.fragments.ListsFragment
import me.samuki.musicandspeed.activities.main.fragments.SongsFragment
import me.samuki.musicandspeed.dagger.modules.binds.MainActivityModule
import me.samuki.musicandspeed.dagger.scopes.ActivityScope

@Module
abstract class FragmentsBindingModule {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    @ActivityScope
    abstract fun provideSongsFragment() : SongsFragment

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    @ActivityScope
    abstract fun provideListsFragment() : ListsFragment

}