package me.samuki.musicandspeed.activities.listcreation.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.listcreation.ListCreationActivity
import me.samuki.musicandspeed.activities.listcreation.adapters.SongsListAdapter.WrappedListItem
import me.samuki.musicandspeed.base.BaseViewModel
import me.samuki.musicandspeed.database.AppDatabase
import me.samuki.musicandspeed.database.daos.IntervalDao
import me.samuki.musicandspeed.database.daos.ListDao
import me.samuki.musicandspeed.extensions.toSongModelList
import me.samuki.musicandspeed.models.HeaderModel
import me.samuki.musicandspeed.models.IntervalModel
import me.samuki.musicandspeed.models.SongModel
import me.samuki.musicandspeed.services.media.MusicLibrary
import javax.inject.Inject


class ListCreationViewModel @Inject constructor(
        private val musicLibrary: MusicLibrary,
        private val listDao: ListDao,
        private val intervalDao: IntervalDao
) : BaseViewModel() {

    private var songsList = emptyList<SongModel>()
    private var firstChosenSongs = emptyList<SongModel>()
    private val currentlyChosenSongs = mutableListOf<WrappedListItem>()
    private var chooseFromAllSongs = true
    private var editedInterval: IntervalModel? = null

    private val _defaultSongsSelection = MutableLiveData<IntervalSettings>().apply {
        value = IntervalSettings(50, -1)
    }
    val defaultSongsSelection = _defaultSongsSelection as LiveData<IntervalSettings>

    private val _listCondition = MutableLiveData<ListCondition>()
    private val _wrappedItems = Transformations.switchMap(_listCondition) { value ->
        val data = MutableLiveData<List<WrappedListItem>>()
        val list = mutableListOf<WrappedListItem>()

        if (value.chosenSongs.isNotEmpty()) {
            list.add(WrappedListItem(HeaderModel(R.string.chosenSongsHeader), true))
            list.addAll(value.chosenSongs)
        }
        list.add(WrappedListItem(HeaderModel(R.string.otherSongsHeader)))

        val otherSongs = if (value.allSongsCondition)
            songsList.toMutableList()
        else
            firstChosenSongs.toMutableList()

        otherSongs.removeAll(currentlyChosenSongs.map { it.item })
        list.addAll(otherSongs.map { WrappedListItem(it) }.sortedBy { it.item.getSortValue() })

        data.value = list
        data
    }
    val wrappedItems = _wrappedItems as LiveData<List<WrappedListItem>>

    private val _currentFragment = MutableLiveData<ListCreationActivity.CreationFragments>().apply {
        value = ListCreationActivity.CreationFragments.LIST_FRAGMENT
    }
    val currentFragment = _currentFragment as LiveData<ListCreationActivity.CreationFragments>

    private val _createdIntervals = MutableLiveData<List<IntervalModel>>().apply {
        value = emptyList()
    }
    val createdIntervals = _createdIntervals as LiveData<List<IntervalModel>>

    fun getSongs() {
        disposable +=
                musicLibrary.provideAllSongsObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onNext = {
                                    songsList = it.toSongModelList()
                                    _listCondition.postValue(
                                            ListCondition(chooseFromAllSongs,
                                                    currentlyChosenSongs)
                                    )
                                }
                        )
    }

    fun saveCurrentlyChosenItems(list: List<WrappedListItem>) {
        currentlyChosenSongs.clear()
        currentlyChosenSongs.addAll(list)
    }

    fun pickVolumeAndSpeed() {
        _currentFragment.postValue(ListCreationActivity.CreationFragments.SETTINGS_FRAGMENT)
    }

    fun connectIntervals(volume: Int, speed: Int) {
        _createdIntervals.value?.let { intervals ->
            intervals.firstOrNull { interval -> interval.intervalSpeed == speed}?.let {
                val songs = currentlyChosenSongs.filter { model -> model.item is SongModel }
                        .map { model -> model.item as SongModel }.toMutableList()
                songs.removeAll(it.songs)
                songs.addAll(it.songs)
            }
        }
        endSongSelection(volume, speed)
    }

    fun endSongSelection(volume: Int, speed: Int) {
        _createdIntervals.value?.let { intervals ->
            val newList = intervals.toMutableList()
            editedInterval?.let { newList.remove(it) }

            val songs = currentlyChosenSongs.filter { it.item is SongModel }.map { it.item as SongModel }

            newList.add(
                    IntervalModel(volume, speed, songs.map { it })
            )
            newList.sortBy { it.intervalSpeed }
            _createdIntervals.postValue(newList)
        }
        _currentFragment.postValue(ListCreationActivity.CreationFragments.SUMMARY_FRAGMENT)
        editedInterval = null
    }

    fun createNewInterval() {
        currentlyChosenSongs.clear()
        _listCondition.postValue(
                ListCondition(chooseFromAllSongs, currentlyChosenSongs)
        )
        _currentFragment.postValue(ListCreationActivity.CreationFragments.LIST_FRAGMENT)
        _defaultSongsSelection.postValue(
                IntervalSettings(50, 50)
        )
    }

    fun editInterval(interval: IntervalModel) {
        editedInterval = interval
        currentlyChosenSongs.clear()
        currentlyChosenSongs.addAll(
                interval.songs.map { WrappedListItem(it, true) }
        )
        _listCondition.postValue(
                ListCondition(chooseFromAllSongs, currentlyChosenSongs)
        )
        _currentFragment.postValue(ListCreationActivity.CreationFragments.LIST_FRAGMENT)
        _defaultSongsSelection.postValue(
                IntervalSettings(interval.volume, interval.intervalSpeed)
        )
    }

    fun intervalIsEditing(): Boolean {
        return editedInterval != null
    }

    data class ListCondition(
            val allSongsCondition: Boolean,
            val chosenSongs: List<WrappedListItem>
    )

    data class IntervalSettings(
            val volume: Int,
            val speed: Int
    )

}