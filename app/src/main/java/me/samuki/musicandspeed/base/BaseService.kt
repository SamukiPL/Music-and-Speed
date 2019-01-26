package me.samuki.musicandspeed.base

import android.app.Service
import dagger.android.AndroidInjection
import me.samuki.musicandspeed.dagger.DaggerServiceComponent


abstract class BaseService : Service() {


    private fun androidInjection() {
        DaggerServiceComponent.builder()
                .setContentResolver(contentResolver)
                .setApplication(application)
                .build()
                .inject(this)
    }

    override fun onCreate() {
        androidInjection()
        super.onCreate()
    }

}