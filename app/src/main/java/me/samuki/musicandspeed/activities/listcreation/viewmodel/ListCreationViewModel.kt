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
import me.samuki.musicandspeed.extensions.toSongModelList
import me.samuki.musicandspeed.models.HeaderModel
import me.samuki.musicandspeed.models.IntervalModel
import me.samuki.musicandspeed.models.SongModel
import me.samuki.musicandspeed.services.media.MusicLibrary
import javax.inject.Inject


class ListCreationViewModel @Inject constructor(
        private val musicLibrary: MusicLibrary
) : BaseViewModel() {

    private var songsList = emptyList<SongModel>()
    private var firstChosenSongs = emptyList<SongModel>()
    private val currentlyChosenSongs = mutableListOf<WrappedListItem>()

    private val _chooseFromAllSongs = MutableLiveData<Boolean>()
    private val _wrappedItems = Transformations.switchMap(_chooseFromAllSongs) { value ->
        val data = MutableLiveData<List<WrappedListItem>>()
        val list = mutableListOf<WrappedListItem>()

        if (currentlyChosenSongs.isNotEmpty()) {
            list.add(WrappedListItem(HeaderModel(R.string.chosenSongsHeader), true))
            list.addAll(currentlyChosenSongs)
        }
        list.add(WrappedListItem(HeaderModel(R.string.otherSongsHeader)))

        val otherSongs = if (value)
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
                                    _chooseFromAllSongs.postValue(true)
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

    fun endSongSelection(volume: Int, speed: Int) {
        _createdIntervals.value?.let { intervals ->
            val newList = intervals.toMutableList()
            newList.add(
                    IntervalModel(volume, speed, currentlyChosenSongs.filter { it.item is SongModel }
                            .map { it.item as SongModel }
                    )
            )
            _createdIntervals.postValue(newList)
        }
        _currentFragment.postValue(ListCreationActivity.CreationFragments.SUMMARY_FRAGMENT)
    }

}