package me.samuki.musicandspeed.activities.main.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import me.samuki.musicandspeed.base.BaseViewModel
import me.samuki.musicandspeed.database.daos.IntervalDao
import me.samuki.musicandspeed.database.daos.ListDao
import me.samuki.musicandspeed.extensions.entitiesToIntervals
import me.samuki.musicandspeed.extensions.toSongModelList
import me.samuki.musicandspeed.models.MusicListModel
import me.samuki.musicandspeed.models.SongModel
import me.samuki.musicandspeed.services.media.MusicLibrary
import me.samuki.musicandspeed.utility.AppPreferences
import javax.inject.Inject


class MainActivityViewModel @Inject constructor(
        private val musicLibrary: MusicLibrary,
        private val listDao: ListDao,
        private val intervalDao: IntervalDao,
        private val appPreferences: AppPreferences
        ) : BaseViewModel() {

    private var currentList = appPreferences.chosenListId
    private var allSongs = emptyList<SongModel>()

    private val _songsList =  MutableLiveData<List<SongModel>>()
    val songsList = _songsList as LiveData<List<SongModel>>

    private val _musicLists = MutableLiveData<List<MusicListModel>>()
    val musicLists = _musicLists as LiveData<List<MusicListModel>>

    private val _newListCanBeCreated = MutableLiveData<Boolean>()
    val newListCanBeCreated = _newListCanBeCreated as LiveData<Boolean>

    fun getSongs() {
        disposable +=
            musicLibrary.provideAllSongsObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = {
                                allSongs = it.toSongModelList()
                                prepareRightSongs()
                            }
                    )
    }

    private fun prepareRightSongs() {
        val intervalsEntities = intervalDao.getAllIntervals(currentList)
        var newList = mutableListOf<SongModel>()
        if (currentList != -1L) {
            val intervals = intervalsEntities.entitiesToIntervals(allSongs)
            intervals.forEach {
                newList.addAll(it.songs)
            }
        } else {
            newList = allSongs.toMutableList()
        }
        _songsList.postValue(newList.distinctBy { it.id })
    }

    fun getMusicLists() {
        val chosenListId = appPreferences.chosenListId
        disposable +=
                listDao.getAllListsRx()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy { lists ->
                            val newList = mutableListOf<MusicListModel>()
                            newList.add(
                                    MusicListModel(-1, "Default", -1L == chosenListId)
                            )
                            lists.forEach {
                                newList.add(
                                        MusicListModel(it.id, it.name, it.id == chosenListId)
                                )
                            }
                            _musicLists.postValue(newList)
                        }
    }

    fun clearNewListName() {
        _newListCanBeCreated.postValue(null)
    }

    fun setNewListName(name: String) {
        val list = listDao.isListExisting(name)
        _newListCanBeCreated.postValue(list == null)
    }

    fun setCurrentlyChosenList(listId: Long) {
        currentList = listId
        appPreferences.chosenListId = listId
        getMusicLists()
        prepareRightSongs()
    }

}