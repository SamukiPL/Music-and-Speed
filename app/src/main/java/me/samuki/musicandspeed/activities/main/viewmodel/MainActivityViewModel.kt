package me.samuki.musicandspeed.activities.main.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.BaseObservable
import io.reactivex.disposables.CompositeDisposable
import me.samuki.musicandspeed.base.BaseViewModel
import me.samuki.musicandspeed.models.SongModel
import javax.inject.Inject


class MainActivityViewModel @Inject constructor() : ViewModel() {

    private val _songsList =  MutableLiveData<List<SongModel>>()
    val songsList = _songsList as LiveData<List<SongModel>>

    val disposable = CompositeDisposable()

    fun getSongs() {
        mockData()
    }

    private fun mockData() {
        val mockedList = mutableListOf<SongModel>()
        mockedList.add(SongModel("Song 1", "Artist 1", "Album 1", "1", "1", 1))
        mockedList.add(SongModel("Song 2", "Artist 2", "Album 2", "1", "1", 1))
        mockedList.add(SongModel("Song 3", "Artist 3", "Album 3", "1", "1", 1))
        mockedList.add(SongModel("Song 4", "Artist 4", "Album 4", "1", "1", 1))
        mockedList.add(SongModel("Song 5", "Artist 5", "Album 5", "1", "1", 1))
        _songsList.postValue(mockedList)
    }

}