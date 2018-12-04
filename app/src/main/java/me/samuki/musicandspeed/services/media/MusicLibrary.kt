package me.samuki.musicandspeed.services.media

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaMetadataCompat
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

import android.support.v4.media.MediaMetadataCompat.*
import javax.inject.Inject


const val MUSIC_LIBRARY_ROOT = "media_root"

class MusicLibrary @Inject constructor(
        private val contentResolver: ContentResolver
) {

    val disposable = CompositeDisposable()

    var musicList: List<LibraryModel> = emptyList()
    var mediaItemList: MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()


    fun provideAllSongsObservable(): Observable<List<LibraryModel>> = Observable.just (
            provideAllSongs()
        )

    private fun provideAllSongs(): List<LibraryModel> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = contentResolver.query(uri, null, selection, null, sortOrder)
        val songsList: MutableList<LibraryModel> = mutableListOf()

        cursor?.let {
            if (it.count > 0) {
                while (it.moveToNext()) {
                    val albumCover = Uri.parse("content://media/external/audio/albumart")
                    val albumCoverUri = ContentUris.withAppendedId(albumCover,
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID)))

                    val metadata = createMetadata(it, albumCoverUri)

                    val mediaItem = MediaBrowserCompat.MediaItem(metadata.description, FLAG_PLAYABLE)
                    mediaItemList.add(mediaItem)

                    songsList.add(
                            LibraryModel (
                                metadata,
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                albumCoverUri
                        )
                    )
                }
            }

        }

        cursor?.close()

        return songsList
    }

    private fun createMetadata(cursor: Cursor, albumCoverUri: Uri): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
                .putString(METADATA_KEY_MEDIA_ID,
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)).toString())
                .putString(METADATA_KEY_ALBUM,
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)))
                .putString(METADATA_KEY_ARTIST,
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)))
                .putLong(METADATA_KEY_DURATION,
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)))
//                                    .putString(METADATA_KEY_GENRE,
//                                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME)))
                .putString(METADATA_KEY_ALBUM_ART_URI, albumCoverUri.path)
                .putString(METADATA_KEY_TITLE,
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)))
                .build()
    }


    data class LibraryModel(
            val metadata: MediaMetadataCompat,
            val musicUri: Uri,
            val albumArtUri: Uri
    )

}