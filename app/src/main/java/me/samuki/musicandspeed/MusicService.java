package me.samuki.musicandspeed;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MusicService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private final int notifyID = 101;

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
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setOngoing(true);
                /*
                //NOTIFICATION INTENT
                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

                notificationBuilder.setContentIntent(pendingIntent)
                        .setContentTitle("Tak")
                        .setTicker("Tak")
                        .addAction(android.R.drawable.ic_media_previous, "Previous", pPreviousIntent)
                        .addAction(android.R.drawable.ic_media_pause, "Pause", pPauseIntent)
                        .addAction(android.R.drawable.ic_media_next, "Next", pNextIntent);
                notification = notificationBuilder.build();
                */
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
                closeIntent.setAction("Close");
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
                } else if (intent.getAction().equals("Close")) {
                    stopForeground(true);
                    stopSelf();
                }

        }

        } catch (NullPointerException e) {
            Log.d(MainActivity.DEBUG_TAG, "TAK I NIE");
        }
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

    private void start() {}
    private void restart() {}
    private void previous() {}
    private void pause() {}
    private void next() {}
    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}
