package me.samuki.musicandspeed.dagger

import android.app.Application
import android.content.ContentResolver
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import me.samuki.musicandspeed.MusicApp
import me.samuki.musicandspeed.dagger.modules.*
import me.samuki.musicandspeed.dagger.scopes.AppScope

@AppScope
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivitiesBindingModule::class,
    FragmentsBindingModule::class,
    UtilitiesModule::class,
    DatabaseModule::class
])
interface AppComponent {

    fun inject(app: MusicApp)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun setContentResolver(contentResolver: ContentResolver): Builder

        @BindsInstance
        fun setApplication(app: Application): Builder
    }

}