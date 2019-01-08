package me.samuki.musicandspeed

import android.app.Activity
import android.app.Application
import android.support.v4.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import me.samuki.musicandspeed.dagger.AppComponent
import me.samuki.musicandspeed.dagger.DaggerAppComponent
import javax.inject.Inject


class MusicApp : Application(), HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        this.appComponent = DaggerAppComponent.builder()
                .setContentResolver(contentResolver)
                .setApplication(this)
                .build()

        this.appComponent.inject(this)
    }


    override fun activityInjector(): AndroidInjector<Activity> = activityInjector
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

}