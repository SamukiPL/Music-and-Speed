package me.samuki.musicandspeed

/**
 * Created by Samuki on 20.11.2017.
 */
interface BasePresenter<V> {
    abstract fun attachView(view: V)
    abstract fun detach()

}