package me.samuki.musicandspeed;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = "Debugujemy";
    private static final long TIME_TO_CHANGE_VOLUME = 5000;

    static MediaPlayer mediaPlayer;
    static Location activityLocation;
    static boolean over50;
    static LayoutInflater inflater;

    private TextView textView;
    static List<String> audioNames;
    private List<String> paths;
    private CountDownTimer timer;
    private boolean isTimerRunning;
    private long timeLeftToChangeVolume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.actualSpeed);
        mediaPlayer = new MediaPlayer();
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
                if(location != null) {
                    MainActivity.activityLocation = new Location(location);
                    updateSpeed();
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
        });
        inflater = getLayoutInflater();
        setAudioNamesList();

        updateSpeed();
        isTimerRunning = false;
        new CountDownTimer(140000, 1000) {

            @Override
            public void onTick(long l) {
                long tymczasem = ((140000 - l)/1000)*5;
                if(tymczasem > 65)
                    updateSpeed(45);
                else
                    updateSpeed(tymczasem);
            }

            @Override
            public void onFinish() {
                updateSpeed();
            }
        }.start();

        Intent startIntent = new Intent(this, MusicService.class);
        startIntent.setAction("Start");
        startService(startIntent);
    }

    public void setAudioNamesList() {
        audioNames = new LinkedList<String>();
        paths = new LinkedList<String>();

        ContentResolver cr = this.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        if(cur != null)
        {
            count = cur.getCount();

            if(count > 0)
            {
                while(cur.moveToNext())
                {
                    String data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    audioNames.add(data);
                    String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    paths.add(path);
                }

            }
        }

        assert cur != null;
        cur.close();
    }

    private void updateSpeed() {
        float speed = 0;
        if(activityLocation != null) {
            speed = activityLocation.getSpeed() * 3.6f;
            Log.d(DEBUG_TAG, activityLocation.getAccuracy() + " ");
        }

        textView.setText(getString(R.string.current_speed, speed));
        if(speed < 50)
            textView.setTextColor(ContextCompat.getColor(this, R.color.green));
        else if(speed > 50 && speed < 80)
            textView.setTextColor(ContextCompat.getColor(this, R.color.yellow));
        else if(speed > 80)
            textView.setTextColor(ContextCompat.getColor(this, R.color.red));
    }
    private void updateSpeed(float speed) {
        textView.setText(getString(R.string.current_speed, speed));
        if(speed < 50) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.green));
            if(over50) {
                over50 = false;
                changeVolumeUp();
                Log.d(DEBUG_TAG, "TAK TAK");
            }
        }
        else if(speed > 50 && speed < 80) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.yellow));
            if(!over50) {
                over50 = true;
                changeVolume();
            }
        }
        else if(speed > 80) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
    }

    public void goToAudioList(View view) {
        Intent audioListIntent = new Intent(this, AudioListActivity.class);
        startActivity(audioListIntent);
    }

    public void playMusic(View view) throws IOException {
        Button button = (Button) view;
        if(((Button) view).getText().equals(getString(R.string.play))) {
            playMusic();
            button.setText(getString(R.string.stop));
        }
        else {
            stopMusic();
            button.setText(getString(R.string.play));
        }
    }
    public void playMusic() throws IOException  {
        Random random = new Random(); //AccessFile
        int playThatOne = random.nextInt(audioNames.size());

        mediaPlayer.setDataSource(paths.get(playThatOne));
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    stopMusic();
                    playMusic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void stopMusic() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }
    public void changeVolume() {
        if(isTimerRunning)
            timer.cancel();
        isTimerRunning = true;
        timer = new CountDownTimer(TIME_TO_CHANGE_VOLUME, 100) {
            @Override
            public void onTick(long l) {
                float lowerVolume = (float)l/(float)TIME_TO_CHANGE_VOLUME;
                mediaPlayer.setVolume(lowerVolume, lowerVolume);
                timeLeftToChangeVolume = l;
            }

            @Override
            public void onFinish() {
                try {
                    stopMusic();
                    playMusic();
                    mediaPlayer.setVolume(1,1);
                } catch (IOException ignored) {
                }
            }
        };
        timer.start();
    }
    public void changeVolumeUp() {
        final long higherTheVolume = TIME_TO_CHANGE_VOLUME - timeLeftToChangeVolume;
        if(isTimerRunning)
            timer.cancel();
        isTimerRunning = true;
        timer = new CountDownTimer(higherTheVolume, 100) {

            @Override
            public void onTick(long l) {
                float lowerVolume = ((float)TIME_TO_CHANGE_VOLUME - (float)l)/(float)TIME_TO_CHANGE_VOLUME;
                Log.d(DEBUG_TAG, lowerVolume + "");
                mediaPlayer.setVolume(lowerVolume, lowerVolume);
            }

            @Override
            public void onFinish() {
                mediaPlayer.setVolume(1, 1);
            }
        };
        timer.start();
    }
}
