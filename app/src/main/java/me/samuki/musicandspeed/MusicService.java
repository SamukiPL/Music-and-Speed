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
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import static me.samuki.musicandspeed.MainActivity.DEBUG_TAG;

public class MusicService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private final int notifyID = 101;

    static MediaPlayer mediaPlayer;
    static Location activityLocation;
    static boolean over50;
    static List<String> audioNames;
    static List<String> paths;
    static MusicPlayerManager playerManager;

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;
    Notification notification;
    RemoteViews remoteViews;

    private Context context;
    private LocationManager locationManager;
    private TextView speedView, titleView;
    private static MusicService thisService = null;

    public MusicService(){}

    public void setSpeedViewAndTitleView(TextView speedView, TextView titleView) {
        this.speedView = speedView;
        this.titleView = titleView;
    }
    public static MusicService getThisService() {
        return thisService;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thisService = this;
        remoteViews = new RemoteViews(getPackageName(), R.layout.music_notification_layout);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Intent nextIntent = new Intent(context, MusicService.class);
                nextIntent.setAction("Next");
                startService(nextIntent);
            }
        });
        playerManager = new MusicPlayerManager(context);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                if(location != null) {
                    MusicService.activityLocation = new Location(location);
                    new MusicService().updateSpeed();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });/*
        new CountDownTimer(140000, 1000) {

            @Override
            public void onTick(long l) {
                long tymczasem = ((140000 - l)/1000)*5;
                Log.d(DEBUG_TAG, String.valueOf(tymczasem));
                if(speedView != null)
                    updateSpeed(tymczasem);
            }

            @Override
            public void onFinish() {
                if(speedView != null)
                    updateSpeed();
            }
        }.start();*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (!intent.getAction().equals("Binding")) {
                context = getApplicationContext();
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentText("Music")
                        .setSmallIcon(R.drawable.default_album_cover)
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
                if(intent.getAction().equals("Start")) {
                    start();
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

                    notificationManager.notify(notifyID, notification);
                    restart();
                } else if (intent.getAction().equals("Previous")) {
                    previous();

                    notification = notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews).build();

                    notificationManager.notify(notifyID, notification);
                } else if (intent.getAction().equals("Pause")) {
                    //START INTENT
                    Intent restartIntent = new Intent(this, MusicService.class);
                    restartIntent.setAction("Restart");
                    PendingIntent pRestartIntent = PendingIntent.getService(this, 0, restartIntent, 0);
                    remoteViews.setOnClickPendingIntent(R.id.pause, pRestartIntent);
                    remoteViews.setImageViewResource(R.id.pause, android.R.drawable.ic_media_play);

                    notification = notificationBuilder.setContentIntent(pRestartIntent)
                            .setContent(remoteViews).build();

                    notificationManager.notify(notifyID, notification);
                    pause();
                } else if (intent.getAction().equals("Next")) {
                    next();

                    notification = notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews).build();

                    notificationManager.notify(notifyID, notification);
                } else if (intent.getAction().equals("Stop")) {
                    stop();
                    stopForeground(true);
                    stopSelf();
                } else if (intent.getAction().equals("Location")) {
                    Log.d(DEBUG_TAG, "TAK TAK TAK");
                    return START_NOT_STICKY;
                }
                Log.d(DEBUG_TAG, String.valueOf(playerManager.isPlaying));
            }
        } catch (NullPointerException ignored) {}
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void start() {
        try {
            playerManager.playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int actualMusicPlayed = playerManager.getActualMusicPlaying();
        remoteViews.setTextViewText(R.id.title, audioNames.get(actualMusicPlayed));
        titleView.setText(audioNames.get(actualMusicPlayed));
    }

    private void restart() {
        playerManager.restartMusic();
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
    }

    private void next() {
        try {
            boolean tmpIsPlaying = playerManager.isPlaying;
            playerManager.stopMusic();//This action gonna change the value of isPlaying!
            if(tmpIsPlaying)
                playerManager.playMusic();
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
    }

    public void updateSpeed() {
        float speed = 0;
        if(activityLocation != null) {
            speed = activityLocation.getSpeed() * 3.6f;
            Log.d(DEBUG_TAG, activityLocation.getAccuracy() + " ");
            if(speed < 40) {
                if(over50) {
                    over50 = false;
                    if(playerManager.isPlaying)
                        playerManager.changeVolumeUp();
                }
            }
            else if(speed > 40) {
                if(!over50) {
                    over50 = true;
                    if(playerManager.isPlaying)
                        playerManager.changeVolumeDown();
                }
            }
        }
        if(speedView != null)
            speedView.setText(getString(R.string.current_speed, speed));
    }

    private void updateSpeed(float speed) {
        if(speed < 50) {
            if(over50) {
                over50 = false;
                if(playerManager.isPlaying)
                    playerManager.changeVolumeUp();
            }
        }
        else if(speed > 50) {
            if(!over50) {
                over50 = true;
                if(playerManager.isPlaying)
                    playerManager.changeVolumeDown();
            }
        }
        speedView.setText(getString(R.string.current_speed, speed));
    }

    class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}
