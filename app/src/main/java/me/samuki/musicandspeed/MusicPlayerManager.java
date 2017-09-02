package me.samuki.musicandspeed;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;

import java.io.IOException;
import java.util.Random;

import static me.samuki.musicandspeed.MainActivity.DEBUG_TAG;
import static me.samuki.musicandspeed.MusicService.audioNames;
import static me.samuki.musicandspeed.MusicService.mediaPlayer;
import static me.samuki.musicandspeed.MusicService.paths;

class MusicPlayerManager {
    private static final long TIME_TO_CHANGE_VOLUME = 5000;

    private Context context;
    private CountDownTimer timer;
    private boolean isTimerRunning;
    private long timeLeftToChangeVolume;

    boolean isPlaying;

    MusicPlayerManager(Context context) {
        this.context = context;
        isTimerRunning = false;
    }

    void playMusic() throws IOException  {
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
        isPlaying = true;
    }

    void restartMusic() {
        mediaPlayer.start();
        isPlaying = true;
    }

    void pauseMusic() {
        mediaPlayer.pause();
        isPlaying = false;
    }

    void stopMusic() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        isPlaying = false;
    }

    void changeVolumeDown() {
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

    void changeVolumeUp() {
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
