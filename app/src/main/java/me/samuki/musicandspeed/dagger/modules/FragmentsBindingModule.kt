package me.samuki.musicandspeed.dagger.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.samuki.musicandspeed.activities.main.fragments.ListsFragment
import me.samuki.musicandspeed.activities.main.fragments.SongsFragment
import me.samuki.musicandspeed.dagger.modules.binds.MainActivityModule

@Module
abstract class FragmentsBindingModule {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun provideSongsFragment() : SongsFragment

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun provideListsFragment() : ListsFragment

}