package me.samuki.musicandspeed.dagger.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.samuki.musicandspeed.activities.main.MainActivity
import me.samuki.musicandspeed.dagger.modules.binds.MainActivityModule

@Module
abstract class ActivitiesBindingModule {

    @ContributesAndroidInjector(modules = [
        MainActivityModule::class
    ])
    abstract fun bindMainActivity(): MainActivity

}