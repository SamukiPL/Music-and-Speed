package me.samuki.musicandspeed;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

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

    public MusicService(){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        remoteViews = new RemoteViews(getPackageName(), R.layout.music_notification_layout);
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
                    remoteViews.setOnClickPendingIntent(R.id.pause, pPauseIntent);
                    notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews);

                    notification = notificationBuilder.build();
                    startForeground(notifyID, notification);
                    start();
                } else if (intent.getAction().equals("Restart")) {
                    remoteViews.setOnClickPendingIntent(R.id.pause, pPauseIntent);
                    remoteViews.setImageViewResource(R.id.pause, android.R.drawable.ic_media_pause);

                    notification = notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews).build();

                    notificationManager.notify(notifyID, notification);
                    restart();
                } else if (intent.getAction().equals("Previous")) {
                    remoteViews.setTextViewText(R.id.title, "Previous!");
                    remoteViews.setImageViewResource(R.id.image, R.mipmap.ic_launcher_round);

                    notification = notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews).build();

                    notificationManager.notify(notifyID, notification);
                    previous();
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
                    remoteViews.setTextViewText(R.id.title, "Next!");
                    remoteViews.setImageViewResource(R.id.image, R.mipmap.ic_launcher);

                    notification = notificationBuilder.setContentIntent(pendingIntent)
                            .setContent(remoteViews).build();

                    notificationManager.notify(notifyID, notification);
                    next();
                } else if (intent.getAction().equals("Stop")) {
                    stop();
                    stopForeground(true);
                    stopSelf();
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
        playerManager = new MusicPlayerManager(context);
        try {
            playerManager.playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restart() {
        playerManager.restartMusic();
    }

    private void previous() {
        playerManager.previousMusic();
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
    }

    private void stop() {
        playerManager.stopMusic();
    }

    public static void updateSpeed() {
        float speed = 0;
        if(activityLocation != null) {
            speed = activityLocation.getSpeed() * 3.6f;
            Log.d(DEBUG_TAG, activityLocation.getAccuracy() + " ");
        }

    }

    private static void updateSpeed(float speed) {
        if(speed < 50) {
            if(over50) {
                over50 = false;
                playerManager.changeVolumeUp();
                Log.d(DEBUG_TAG, "TAK TAK");
            }
        }
        else if(speed > 50 && speed < 80) {
            if(!over50) {
                over50 = true;
                playerManager.changeVolumeDown();
            }
        }
        else if(speed > 80) {
        }
    }

    class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}
