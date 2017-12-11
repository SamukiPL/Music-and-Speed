package me.samuki.musicandspeed.audiolist

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import me.samuki.musicandspeed.R


class AudioListActivity : AppCompatActivity(), AudioListView {

    lateinit private var presenter: AudioListPresenter<AudioListView>

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_audio_list)
        start()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun start() {
        presenter = AudioListPresenterImpl()
        presenter.attachView(this)
    }

    override fun stop() {
        presenter.detach()
    }

    override fun showProgressBar() {

    }

    override fun getContext(): Context {
        return getContext()
    }
}