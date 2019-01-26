package me.samuki.musicandspeed.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.DialogFragment
import dagger.android.support.DaggerDialogFragment


open class BaseDialogFragment : DaggerDialogFragment() {

    inline fun <reified A : BaseActivity, reified T : ViewModel> provideActivityViewModel(): T? =
            (activity as? A)?.run {
                ViewModelProviders.of(this).get(T::class.java)
            }

}