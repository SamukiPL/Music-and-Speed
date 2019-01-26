package me.samuki.musicandspeed.services.musicplayer

import android.Manifest
import android.app.*
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationManager.IMPORTANCE_NONE
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import android.widget.TextView
import dagger.android.DaggerService
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.activities.player.PlayerActivity
import me.samuki.musicandspeed.base.BaseService
import me.samuki.musicandspeed.dagger.DaggerServiceComponent
import me.samuki.musicandspeed.database.AppDatabase
import me.samuki.musicandspeed.database.daos.IntervalDao
import me.samuki.musicandspeed.extensions.entitiesToIntervals
import me.samuki.musicandspeed.extensions.toSongModelList
import me.samuki.musicandspeed.models.IntervalModel
import me.samuki.musicandspeed.services.media.MusicLibrary
import me.samuki.musicandspeed.utility.AppPreferences
import me.samuki.musicandspeed.utility.BundleConstants
import java.lang.Exception
import javax.inject.Inject

const val notifyID = 86

class MusicPlayerService : BaseService() {

    lateinit var intervalDao: IntervalDao

    lateinit var preferences: AppPreferences

    lateinit var notificationManager: NotificationManager
    lateinit var notificationBuilder: NotificationCompat.Builder
    lateinit var notification: Notification
    private lateinit var remoteViews: RemoteViews

    val playerManager = MusicPlayerManager()

    override fun onBind(intent: Intent?): IBinder? {
        return LocalBinder()
    }

    override fun onCreate() {
        super.onCreate()
        setLocationManager()
        remoteViews = RemoteViews(packageName, R.layout.music_notification_layout)

        preferences = AppPreferences(application)
        val db = Room.inMemoryDatabaseBuilder(
                this, AppDatabase::class.java).build()
        intervalDao = db.getIntervalDao()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {
            if (playerManager.fullSongsList.isEmpty())
                playerManager.fullSongsList = MusicLibrary(contentResolver).provideAllSongs().toSongModelList()
            setIntervals()
            if (intent.action != "Binding") {
                notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                createNotificationChannel(notificationManager)

                notificationBuilder = NotificationCompat.Builder(this, notifyID.toString())
                        .setContentText("Music")
                        .setSmallIcon(R.drawable.initial_music_logo_alpha_transparent)
                        .setOngoing(true)
                //NOTIFICATION INTENT
                val notificationIntent = Intent(this, PlayerActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
                //PREVIOUS INTENT
                val previousIntent = Intent(this, MusicPlayerService::class.java)
                previousIntent.action = "Previous"
                val pPreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0)
                //PAUSE INTENT
                val pauseIntent = Intent(this, MusicPlayerService::class.java)
                pauseIntent.action = "Pause"
                val pPauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0)
                //RESTART INTENT
                val restartIntent = Intent(this, MusicPlayerService::class.java)
                restartIntent.action = "Restart"
                val pRestartIntent = PendingIntent.getService(this, 0, restartIntent, 0)
                //NEXT INTENT
                val nextIntent = Intent(this, MusicPlayerService::class.java)
                nextIntent.action = "Next"
                val pNextIntent = PendingIntent.getService(this, 0, nextIntent, 0)
                //CLOSE INTENT
                val closeIntent = Intent(this, MusicPlayerService::class.java)
                closeIntent.action = "Stop"
                val pCloseIntent = PendingIntent.getService(this, 0, closeIntent, 0)

                remoteViews.setOnClickPendingIntent(R.id.previous, pPreviousIntent)
                remoteViews.setOnClickPendingIntent(R.id.next, pNextIntent)
                remoteViews.setOnClickPendingIntent(R.id.close, pCloseIntent)
                if (intent.action == "Start" || playerManager.firstServicePlay && !(intent.action == "Next" || intent.action == "Previous")) {
                    val songId = intent.getStringExtra(BundleConstants.SONG_ID)
                    if (songId.isEmpty())
                        playerManager.startMusic()
                    else
                        playerManager.startMusic(songId)

                    remoteViews.setOnClickPendingIntent(R.id.pause, pPauseIntent)
                    notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews)

                    notification = notificationBuilder.build()
                    playerManager.currentSong?.name?.let {
                        remoteViews.setTextViewText(R.id.title, it)
                    }
                    startForeground(notifyID, notification)
                } else if (intent.action == "Restart") {
                    remoteViews.setOnClickPendingIntent(R.id.pause, pPauseIntent)
                    remoteViews.setImageViewResource(R.id.pause, android.R.drawable.ic_media_pause)

                    notification = notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews).build()

                    if (playerManager.firstServicePlay) startForeground(notifyID, notification)


                    playerManager.restartMusic()
                    setPlayButtonOnNotification(pPauseIntent, pRestartIntent)
                    notificationManager.notify(notifyID, notification)
                    playerManager.currentSong?.name?.let {
                        remoteViews.setTextViewText(R.id.title, it)
                    }
                    startForeground(notifyID, notification)
                } else if (intent.action == "Previous") {
                    setPlayButtonOnNotification(pPauseIntent, pRestartIntent)
                    playerManager.previousMusic()

                    notification = notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews).build()

                    if (playerManager.firstServicePlay) startForeground(notifyID, notification)

                    notificationManager.notify(notifyID, notification)
                    playerManager.currentSong?.name?.let {
                        remoteViews.setTextViewText(R.id.title, it)
                    }
                    startForeground(notifyID, notification)
                } else if (intent.action == "Pause") {
                    remoteViews.setOnClickPendingIntent(R.id.pause, pRestartIntent)
                    remoteViews.setImageViewResource(R.id.pause, android.R.drawable.ic_media_play)

                    notification = notificationBuilder.setContentIntent(pRestartIntent)
                            .setContent(remoteViews).build()

                    if (playerManager.firstServicePlay) startForeground(notifyID, notification)

                    playerManager.pause()

                    setPlayButtonOnNotification(pPauseIntent, pRestartIntent)
                    notificationManager.notify(notifyID, notification)
                    playerManager.currentSong?.name?.let {
                        remoteViews.setTextViewText(R.id.title, it)
                    }
                    startForeground(notifyID, notification)
                } else if (intent.action == "Next") {
                    setPlayButtonOnNotification(pPauseIntent, pRestartIntent)
                    playerManager.nextMusic()

                    notification = notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews).build()

                    if (playerManager.firstServicePlay) startForeground(notifyID, notification)

                    notificationManager.notify(notifyID, notification)
                    playerManager.currentSong?.name?.let {
                        remoteViews.setTextViewText(R.id.title, it)
                    }
                    startForeground(notifyID, notification)
                } else if (intent.action == "Stop") {
                    playerManager.stopMusic()
                    stopForeground(true)
                    stopSelf()
                } else if (intent.action == "Location") {
                    return Service.START_NOT_STICKY
                }
            }
        } catch (e: Exception) {
            e.fillInStackTrace()
        }
        return Service.START_STICKY
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(notifyID.toString(), "Initial Music", IMPORTANCE_NONE)
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setLocationManager() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, object: LocationListener {
            override fun onLocationChanged(location: Location?) {
                location?.let {
                    playerManager.steerIntervals(it.speed)
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

            override fun onProviderEnabled(provider: String?) {}

            override fun onProviderDisabled(provider: String?) {}
        })
    }

    private fun setPlayButtonOnNotification(pPauseIntent: PendingIntent,
                                            pRestartIntent: PendingIntent) {
        if (playerManager.isPlaying) {
            remoteViews.setOnClickPendingIntent(R.id.pause, pPauseIntent)
            remoteViews.setImageViewResource(R.id.pause, android.R.drawable.ic_media_pause)
        } else {
            remoteViews.setOnClickPendingIntent(R.id.pause, pRestartIntent)
            remoteViews.setImageViewResource(R.id.pause, android.R.drawable.ic_media_play)
        }
    }

    private fun setIntervals() {
        val listId = preferences.chosenListId
        if (listId != -1L) {
            val intervalEntities = intervalDao.getAllIntervals(listId)
            playerManager.intervalsList = intervalEntities.entitiesToIntervals(playerManager.fullSongsList)
                    .sortedBy { it.intervalSpeed }
            playerManager.currentList = playerManager.intervalsList.first { it.intervalSpeed == -1 }.songs
        } else {
            playerManager.currentList = playerManager.fullSongsList
            playerManager.intervalsList = listOf(IntervalModel(100, -1, playerManager.fullSongsList))
        }
    }

    inner class LocalBinder: Binder() {

        fun getService(): MusicPlayerService {
            return this@MusicPlayerService
        }

    }

}