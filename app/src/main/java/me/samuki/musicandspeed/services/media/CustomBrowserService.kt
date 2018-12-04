package me.samuki.musicandspeed.services.media

import android.media.session.MediaSession
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

private const val MY_MEDIA_LOG_TAG = "Media Browser"

class CustomBrowserService : MediaBrowserServiceCompat() {

    private var mediaSession: MediaSessionCompat? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var musicLibrary: MusicLibrary

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(baseContext, MY_MEDIA_LOG_TAG).apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            stateBuilder = PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE)

            setPlaybackState(stateBuilder.build())

            setCallback(MediaCallback())

            setSessionToken(sessionToken)
        }
        musicLibrary = MusicLibrary(contentResolver)
    }

    override fun onLoadChildren(parentMediaId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(musicLibrary.mediaItemList)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(MUSIC_LIBRARY_ROOT, null)
    }

}