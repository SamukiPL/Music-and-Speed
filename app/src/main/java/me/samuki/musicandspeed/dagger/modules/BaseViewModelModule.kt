package me.samuki.musicandspeed.dagger.modules

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.samuki.musicandspeed.base.BaseViewModel
import me.samuki.musicandspeed.dagger.scopes.ActivityScope
import me.samuki.musicandspeed.utilityvm.ViewModelFactory
import me.samuki.musicandspeed.utilityvm.ViewModelKey

@Module
abstract class BaseViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}