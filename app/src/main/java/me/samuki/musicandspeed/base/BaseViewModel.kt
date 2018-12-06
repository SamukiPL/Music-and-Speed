package me.samuki.musicandspeed.base

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable


open class BaseViewModel : ViewModel() {

    protected val disposable = CompositeDisposable()

}