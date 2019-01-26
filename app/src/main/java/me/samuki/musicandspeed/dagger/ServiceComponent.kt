package me.samuki.musicandspeed.dagger

import android.app.Application
import android.content.ContentResolver
import dagger.BindsInstance
import dagger.Component
import me.samuki.musicandspeed.base.BaseService
import me.samuki.musicandspeed.dagger.modules.DatabaseModule
import me.samuki.musicandspeed.dagger.modules.ServicesBindingModule
import me.samuki.musicandspeed.dagger.modules.UtilitiesModule
import me.samuki.musicandspeed.dagger.scopes.AppScope
import me.samuki.musicandspeed.dagger.scopes.ServiceScope

@AppScope
@Component(
        modules = [
            ServicesBindingModule::class,
            UtilitiesModule::class,
            DatabaseModule::class
        ]
)
interface ServiceComponent {

    fun inject(service: BaseService)

    @Component.Builder
    interface Builder {
        fun build(): ServiceComponent

        @BindsInstance
        fun setContentResolver(contentResolver: ContentResolver): Builder

        @BindsInstance
        fun setApplication(app: Application): Builder
    }

}