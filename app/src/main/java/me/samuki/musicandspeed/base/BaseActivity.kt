package me.samuki.musicandspeed.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.FitWindowsFrameLayout
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import dagger.android.AndroidInjection
import me.samuki.musicandspeed.R


abstract class BaseActivity(private val showToolbar: Boolean) : AppCompatActivity() {

    private fun androidInjection() {
        AndroidInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        androidInjection()
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(prepareLayout(layoutResID))
    }

    override fun onStart() {
        super.onStart()
        setToolbar()
    }

    private fun prepareLayout(layoutResID: Int): LinearLayout {
        val coordinator = LinearLayout(this).also {
            it.fitsSystemWindows = true
            it.orientation = LinearLayout.VERTICAL
        }

        val contentView = layoutInflater.inflate(layoutResID, coordinator, false)
        val actionBar = layoutInflater.inflate(R.layout.toolbar, coordinator, false)

        if (showToolbar)
            coordinator.addView(actionBar)
        coordinator.addView(contentView)

        return coordinator
    }

    private fun setToolbar() {
        findViewById<Toolbar>(R.id.toolbar)?.let {
            if (supportActionBar == null)
                setSupportActionBar(it)
        }
    }

}