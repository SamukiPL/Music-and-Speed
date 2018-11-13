package me.samuki.musicandspeed.dagger.modules.binds

import android.arch.lifecycle.ViewModel
import android.support.v4.app.FragmentPagerAdapter
import dagger.Binds
import dagger.Module
import me.samuki.musicandspeed.activities.main.fragments.MainPagerAdapter
import me.samuki.musicandspeed.activities.main.viewmodel.MainActivityViewModel

@Module
abstract class MainActivityModule {

    @Binds
    abstract fun bindMainActivityViewModule(mainActivityViewModel: MainActivityViewModel): ViewModel

}