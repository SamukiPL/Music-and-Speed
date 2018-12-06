package me.samuki.musicandspeed.extensions

import android.view.View


fun View.onClick(action: () -> Unit) {
    setOnClickListener {
        action.invoke()
    }
}