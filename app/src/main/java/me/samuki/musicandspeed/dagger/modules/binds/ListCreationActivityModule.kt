package me.samuki.musicandspeed.dagger.modules.binds

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.samuki.musicandspeed.activities.listcreation.viewmodel.ListCreationViewModel
import me.samuki.musicandspeed.activities.main.viewmodel.MainActivityViewModel
import me.samuki.musicandspeed.dagger.modules.BaseViewModelModule
import me.samuki.musicandspeed.utilityvm.ViewModelFactory
import me.samuki.musicandspeed.utilityvm.ViewModelKey

@Module
abstract class ListCreationActivityModule : BaseViewModelModule() {

    @Binds
    @IntoMap
    @ViewModelKey(ListCreationViewModel::class)
    abstract fun bindListCreationViewmodel(listCreationViewModel: ListCreationViewModel): ViewModel

}