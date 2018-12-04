package me.samuki.musicandspeed.dagger.modules.binds

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.samuki.musicandspeed.activities.main.viewmodel.MainActivityViewModel
import me.samuki.musicandspeed.dagger.modules.BaseViewModelModule
import me.samuki.musicandspeed.utilityvm.ViewModelKey

@Module
abstract class MainActivityModule : BaseViewModelModule() {

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModule(mainActivityViewModel: MainActivityViewModel): ViewModel

}