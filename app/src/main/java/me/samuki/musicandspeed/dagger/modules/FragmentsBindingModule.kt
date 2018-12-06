package me.samuki.musicandspeed.dagger.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.samuki.musicandspeed.activities.listcreation.fragments.CreationSettingsFragment
import me.samuki.musicandspeed.activities.listcreation.fragments.CreationSongsListFragment
import me.samuki.musicandspeed.activities.listcreation.fragments.CreationSummaryFragment
import me.samuki.musicandspeed.activities.main.fragments.ListsFragment
import me.samuki.musicandspeed.activities.main.fragments.SongsFragment
import me.samuki.musicandspeed.dagger.modules.binds.ListCreationActivityModule
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

    @ContributesAndroidInjector(modules = [ListCreationActivityModule::class])
    @ActivityScope
    abstract fun provideSummaryFragment() : CreationSummaryFragment

    @ContributesAndroidInjector(modules = [ListCreationActivityModule::class])
    @ActivityScope
    abstract fun provideSongsListFragment() : CreationSongsListFragment

    @ContributesAndroidInjector(modules = [ListCreationActivityModule::class])
    @ActivityScope
    abstract fun provideListSettingsFragment() : CreationSettingsFragment

}