package me.samuki.musicandspeed.activities.main.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.ContentResolver
import android.databinding.BaseObservable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import me.samuki.musicandspeed.base.BaseViewModel
import me.samuki.musicandspeed.extensions.toSongModelList
import me.samuki.musicandspeed.models.SongModel
import me.samuki.musicandspeed.services.media.MusicLibrary
import javax.inject.Inject


class MainActivityViewModel @Inject constructor(
        private val musicLibrary: MusicLibrary
) : BaseViewModel() {

    private val _songsList =  MutableLiveData<List<SongModel>>()
    val songsList = _songsList as LiveData<List<SongModel>>

    fun getSongs() {
        disposable +=
            musicLibrary.provideAllSongsObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = {
                                _songsList.postValue(it.toSongModelList())
                            }
                    )
    }

}