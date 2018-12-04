package me.samuki.musicandspeed.dagger.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.samuki.musicandspeed.activities.main.MainActivity
import me.samuki.musicandspeed.dagger.modules.binds.MainActivityModule
import me.samuki.musicandspeed.dagger.scopes.ActivityScope

@Module
abstract class ActivitiesBindingModule {

    @ContributesAndroidInjector(modules = [
        MainActivityModule::class
    ])
    @ActivityScope
    abstract fun bindMainActivity(): MainActivity

}