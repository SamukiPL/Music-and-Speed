package me.samuki.musicandspeed.base

import android.databinding.adapters.ViewGroupBindingAdapter
import android.view.animation.Animation


class AnimationListenerAdapter(
        private val onRepeat: () -> Unit = {},
        private val onEnd: () -> Unit = {},
        private val onStart: () -> Unit = {}
) : Animation.AnimationListener {

    override fun onAnimationRepeat(animation: Animation?) {
        onRepeat.invoke()
    }

    override fun onAnimationEnd(animation: Animation?) {
        onEnd.invoke()
    }

    override fun onAnimationStart(animation: Animation?) {
        onStart.invoke()
    }
}