package me.samuki.musicandspeed.dagger

import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import me.samuki.musicandspeed.MusicApp
import me.samuki.musicandspeed.dagger.modules.ActivitiesBindingModule
import me.samuki.musicandspeed.dagger.modules.FragmentsBindingModule


@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivitiesBindingModule::class,
    FragmentsBindingModule::class
])
interface AppComponent {

    fun inject(app: MusicApp)

}