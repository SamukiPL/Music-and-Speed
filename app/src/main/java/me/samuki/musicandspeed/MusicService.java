package me.samuki.musicandspeed;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import static me.samuki.musicandspeed.MainActivity.DEBUG_TAG;

public class MusicService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private final int notifyID = 86;

    static Location activityLocation;
    static int speedToExceed;
    static boolean overSpeed;
    public static List<String> audioNames, audioArtists, audioPaths;
    public static List<Long> audioDurations;
    static List<Integer> slowDrivingSongs, fastDrivingSongs;
    static MusicPlayerManager playerManager;

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;
    Notification notification;
    RemoteViews remoteViews;

    private Context context;
    private TextView speedView, titleView;
    private ImageButton playButton;

    public MusicService(){}

    public void setSpeedViewAndTitleViewAndPlayButton(TextView speedView, TextView titleView, ImageButton playButton) {
        this.speedView = speedView;
        this.titleView = titleView;
        this.titleView.setText(audioNames.get(playerManager.getActualMusicPlaying()));
        this.playButton = playButton;
        if(playerManager.isPlaying) {
            playButton.setContentDescription(getString(R.string.stop));
            playButton.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
        }
        else {
            playButton.setContentDescription(getString(R.string.play));
            playButton.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        remoteViews = new RemoteViews(getPackageName(), R.layout.music_notification_layout);

        playerManager = new MusicPlayerManager();

        playerManager.mediaPlayer = new MediaPlayer();
        playerManager.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playerManager.mediaPlayer.start();
            }
        });
        playerManager.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Intent nextIntent = new Intent(context, MusicService.class);
                nextIntent.setAction("Next");
                if(!(overSpeed || playerManager.fastDrivingModeActive))
                    playerManager.fastDrivingModeActive = false;
                startService(nextIntent);
            }
        });
        playerManager.firstServicePlay = true;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(DEBUG_TAG, "Changed");
                if(location != null) {
                    MusicService.activityLocation = new Location(location);
                    updateSpeed();
                    Log.d(DEBUG_TAG, "Zmieniono");
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d(DEBUG_TAG, s);
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d(DEBUG_TAG, s);

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(DEBUG_TAG, s);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (!intent.getAction().equals("Binding")) {
                context = getApplicationContext();
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentText("Music")
                        .setSmallIcon(R.drawable.initial_music_logo_alpha_transparent)
                        .setOngoing(true);
                //NOTIFICATION INTENT
                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                //PREVIOUS INTENT
                Intent previousIntent = new Intent(this, MusicService.class);
                previousIntent.setAction("Previous");
                PendingIntent pPreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);
                //PAUSE INTENT
                Intent pauseIntent = new Intent(this, MusicService.class);
                pauseIntent.setAction("Pause");
                PendingIntent pPauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
                //RESTART INTENT
                Intent restartIntent = new Intent(this, MusicService.class);
                restartIntent.setAction("Restart");
                PendingIntent pRestartIntent = PendingIntent.getService(this, 0, restartIntent, 0);
                //NEXT INTENT
                Intent nextIntent = new Intent(this, MusicService.class);
                nextIntent.setAction("Next");
                PendingIntent pNextIntent = PendingIntent.getService(this, 0, nextIntent, 0);
                //CLOSE INTENT
                Intent closeIntent = new Intent(this, MusicService.class);
                closeIntent.setAction("Stop");
                PendingIntent pCloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

                remoteViews.setOnClickPendingIntent(R.id.previous, pPreviousIntent);
                remoteViews.setOnClickPendingIntent(R.id.next, pNextIntent);
                remoteViews.setOnClickPendingIntent(R.id.close, pCloseIntent);
                if(intent.getAction().equals("Start") ||
                        playerManager.firstServicePlay &&
                                !(intent.getAction().equals("Next") ||
                                  intent.getAction().equals("Previous"))) {
                    int trackId = intent.getIntExtra("trackId", -1);
                    start(trackId);
                    remoteViews.setOnClickPendingIntent(R.id.pause, pPauseIntent);
                    notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews);

                    notification = notificationBuilder.build();
                    startForeground(notifyID, notification);
                } else if (intent.getAction().equals("Restart")) {
                    remoteViews.setOnClickPendingIntent(R.id.pause, pPauseIntent);
                    remoteViews.setImageViewResource(R.id.pause, android.R.drawable.ic_media_pause);


                    notification = notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews).build();

                    if(playerManager.firstServicePlay) startForeground(notifyID, notification);

                    notificationManager.notify(notifyID, notification);

                    restart();
                    setPlayButtonOnNotification(pPauseIntent, pRestartIntent);
                } else if (intent.getAction().equals("Previous")) {
                    previous();

                    notification = notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews).build();

                    if(playerManager.firstServicePlay) startForeground(notifyID, notification);

                    notificationManager.notify(notifyID, notification);

                    setPlayButtonOnNotification(pPauseIntent, pRestartIntent);
                } else if (intent.getAction().equals("Pause")) {
                    remoteViews.setOnClickPendingIntent(R.id.pause, pRestartIntent);
                    remoteViews.setImageViewResource(R.id.pause, android.R.drawable.ic_media_play);

                    notification = notificationBuilder.setContentIntent(pRestartIntent)
                            .setContent(remoteViews).build();

                    if(playerManager.firstServicePlay) startForeground(notifyID, notification);

                    notificationManager.notify(notifyID, notification);
                    pause();

                    setPlayButtonOnNotification(pPauseIntent, pRestartIntent);
                } else if (intent.getAction().equals("Next")) {
                    next();


                    notification = notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews).build();

                    if(playerManager.firstServicePlay) startForeground(notifyID, notification);

                    notificationManager.notify(notifyID, notification);


                    setPlayButtonOnNotification(pPauseIntent, pRestartIntent);
                } else if (intent.getAction().equals("Stop")) {
                    stop();
                    stopForeground(true);
                    stopSelf();
                } else if (intent.getAction().equals("Location")) {
                    return START_NOT_STICKY;
                }
            }
        } catch (NullPointerException ignored) {}
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void start(int trackId) {
        int actualMusicPlayed = 0;
        try {
            if(trackId >= 0) {
                playerManager.playMusic(trackId, false);
                actualMusicPlayed = trackId;
            }
            else {
                playerManager.playMusic();
                actualMusicPlayed = playerManager.getActualMusicPlaying();
            }
            playerManager.firstServicePlay = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        remoteViews.setTextViewText(R.id.title, audioNames.get(actualMusicPlayed));
        titleView.setText(audioNames.get(actualMusicPlayed));
    }

    private void restart() {
        playerManager.restartMusic();
        playButton.setContentDescription(getString(R.string.stop));
        playButton.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
    }

    private void previous() {
        playerManager.previousMusic();
        int actualMusicPlayed = playerManager.getActualMusicPlaying();
        remoteViews.setTextViewText(R.id.title, audioNames.get(actualMusicPlayed));
        remoteViews.setImageViewResource(R.id.image, R.mipmap.ic_launcher_round);
        titleView.setText(audioNames.get(actualMusicPlayed));
    }

    private void pause() {
        playerManager.pauseMusic();
        playButton.setContentDescription(getString(R.string.play));
        playButton.setImageResource(android.R.drawable.ic_media_play);
    }

    private void next() {
        try {
            playerManager.nextMusic(playerManager.isPlaying);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int actualMusicPlayed = playerManager.getActualMusicPlaying();
        remoteViews.setTextViewText(R.id.title, audioNames.get(actualMusicPlayed));
        remoteViews.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        titleView.setText(audioNames.get(actualMusicPlayed));
    }

    private void stop() {
        playerManager.stopMusic();
        playerManager.firstServicePlay = true;
        playButton.setContentDescription(getString(R.string.play));
        playButton.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
    }

    public void updateSpeed() {
        float speed = 0;
        if(activityLocation != null) {
            speed = activityLocation.getSpeed() * 3.6f;
            Log.d(DEBUG_TAG, activityLocation.getAccuracy() + " ");
            if(speed < speedToExceed) {
                if(overSpeed) {
                    overSpeed = false;
                    if(playerManager.isPlaying)
                        playerManager.changeVolumeUp();
                }
            }
            else if(speed > speedToExceed) {
                if(!overSpeed) {
                    overSpeed = true;
                    if(playerManager.isPlaying)
                        playerManager.changeVolumeDown();
                }
            }
        }
        speedView.setText(getString(R.string.current_speed, speed));
    }

    private void updateSpeed(float speed) {
        if(speed < 50) {
            if(overSpeed) {
                overSpeed = false;
                if(playerManager.isPlaying && !playerManager.fastDrivingModeActive)
                    playerManager.changeVolumeUp();
            }
        }
        else if(speed > 50) {
            if(!overSpeed) {
                overSpeed = true;
                if(playerManager.isPlaying)
                    playerManager.changeVolumeDown();
            }
        }
        speedView.setText(getString(R.string.current_speed, speed));
    }

    private void setPlayButtonOnNotification(PendingIntent pPauseIntent,
                                             PendingIntent pRestartIntent) {
        if (playerManager.isPlaying) {
            remoteViews.setOnClickPendingIntent(R.id.pause, pPauseIntent);
            remoteViews.setImageViewResource(R.id.pause, android.R.drawable.ic_media_pause);
        } else {
            remoteViews.setOnClickPendingIntent(R.id.pause, pRestartIntent);
            remoteViews.setImageViewResource(R.id.pause, android.R.drawable.ic_media_play);
        }
    }

    class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}
