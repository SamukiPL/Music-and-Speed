package me.samuki.musicandspeed.activities.listcreation.adapters

import android.view.ViewGroup
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import me.samuki.musicandspeed.models.ListModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.mockito.Mockito

internal class SongsListAdapterTest {

    private var songsListAdapter: SongsListAdapter? = null

    @BeforeEach
    fun setUp() {
        songsListAdapter = SongsListAdapter()

        val item = mock<ListModel>()
        whenever(item.getDiff()).thenReturn("1")

        val wrappedMockChosen = mock<SongsListAdapter.WrappedListItem>()
        whenever(wrappedMockChosen.item).thenReturn(item)
        whenever(wrappedMockChosen.chosen).thenReturn(true)
        val wrappedMockNotChosen = Mockito.mock(SongsListAdapter.WrappedListItem::class.java)
        whenever(wrappedMockNotChosen.item).thenReturn(item)
        whenever(wrappedMockNotChosen.chosen).thenReturn(false)
        val itemList = listOf<SongsListAdapter.WrappedListItem>(
                wrappedMockNotChosen, wrappedMockNotChosen, wrappedMockNotChosen,
                wrappedMockChosen, wrappedMockChosen)

        songsListAdapter?.itemList = itemList
    }

    @AfterEach
    fun tearDown() {
        songsListAdapter = null
    }

    @Test
    fun getItemList() {
        val list = songsListAdapter?.itemList

        Assertions.assertEquals(true, list != null)
    }

    @Test
    fun setItemList() {
        val newList = songsListAdapter?.itemList?.toMutableList()
        newList?.let {
            it.removeAt(0)
            it.removeAt(0)

            songsListAdapter?.itemList = it
            Assertions.assertEquals(it.size, songsListAdapter?.itemList?.size)
        }
    }

    @Test
    fun changeItemState() {
    }

    @Test
    fun manageHeader() {
    }

    @Test
    fun getAllChosenItems() {
    }
}