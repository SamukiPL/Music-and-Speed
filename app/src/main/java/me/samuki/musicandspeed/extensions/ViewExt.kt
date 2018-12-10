package me.samuki.musicandspeed.extensions

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


fun View.onClick(action: () -> Unit) {
    setOnClickListener {
        action.invoke()
    }
}

fun ViewGroup.inflate(@LayoutRes resource: Int, attachToRoot: Boolean): View{
    return LayoutInflater.from(context).inflate(resource, this, attachToRoot)
}