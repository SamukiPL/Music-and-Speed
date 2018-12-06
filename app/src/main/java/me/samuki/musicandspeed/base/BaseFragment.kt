package me.samuki.musicandspeed.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import me.samuki.musicandspeed.activities.listcreation.ListCreationActivity
import me.samuki.musicandspeed.activities.main.viewmodel.MainActivityViewModel


open class BaseFragment : DaggerFragment() {

    inline fun <reified A : BaseActivity, reified T : ViewModel> provideActivityViewModel(): T? =
            (activity as? A)?.run {
                ViewModelProviders.of(this).get(T::class.java)
            }

}