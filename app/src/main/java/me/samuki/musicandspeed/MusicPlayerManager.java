package me.samuki.musicandspeed;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static me.samuki.musicandspeed.MainActivity.DEBUG_TAG;
import static me.samuki.musicandspeed.MusicService.audioNames;
import static me.samuki.musicandspeed.MusicService.mediaPlayer;
import static me.samuki.musicandspeed.MusicService.paths;

class MusicPlayerManager {
    private static final long TIME_TO_CHANGE_VOLUME = 5000;
    private static final int rememberThatMany = 50;
    private static final int identificationNumber = -1;

    private Context context;
    private CountDownTimer timer;
    private boolean isTimerRunning;
    private long timeLeftToChangeVolume;
    private int lastMusicPlayed[];
    private int remembered;
    private int actualMusicPlaying;

    boolean isPlaying;

    int getActualMusicPlaying() {
        return actualMusicPlaying;
    }

    MusicPlayerManager(Context context) {
        this.context = context;
        isTimerRunning = false;
        setLastMusicPlayed();
    }

    void playMusic() throws IOException  {
        Random random = new Random(); //AccessFile
        int playThatOne = random.nextInt(audioNames.size());

        mediaPlayer.setDataSource(paths.get(playThatOne));
        mediaPlayer.prepareAsync();
        isPlaying = true;
        addToLastMusicPlayed(playThatOne);
        actualMusicPlaying = playThatOne;
    }

    void playMusic(int whichMusic) throws IOException {
        mediaPlayer.setDataSource(paths.get(whichMusic));
        mediaPlayer.prepareAsync();
        isPlaying = true;
    }

    void playLastMusic() throws IOException {
        actualMusicPlaying = getLastOnePlayed();
        mediaPlayer.setDataSource(paths.get(actualMusicPlaying));
        mediaPlayer.prepareAsync();
        isPlaying = true;
    }

    void restartMusic() {
        try {
            playMusic(actualMusicPlaying);
            isPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void previousMusic() {
        try {
            boolean tmpIsPlaying = isPlaying;
            stopMusic();//This action gonna change the value of isPlaying!
            if(tmpIsPlaying)
                playLastMusic();
            else
                actualMusicPlaying = getLastOnePlayed();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void setLastMusicPlayed() {
        lastMusicPlayed = new int[rememberThatMany];
        Arrays.fill(lastMusicPlayed, identificationNumber);
    }

    private void addToLastMusicPlayed(int number) {
        for (int i = 0; i < rememberThatMany; i++) {
            Log.d(DEBUG_TAG, number+"");
            if(lastMusicPlayed[i] == identificationNumber) {
                lastMusicPlayed[i] = number;
                remembered = i;
                break;
            }
            else {
                int tmp = number;
                number = lastMusicPlayed[i];
                lastMusicPlayed[i] = tmp;
            }
        }
    }

    private int getLastOnePlayed() {
        int number = lastMusicPlayed[remembered];
        if(remembered != 0) {
            lastMusicPlayed[remembered] = identificationNumber;
            for (int i = remembered; i > 0; i--) {
                    int tmp = number;
                    number = lastMusicPlayed[i - 1];
                    lastMusicPlayed[i - 1] = tmp;
            }
        }
        remembered--;
        Log.d(DEBUG_TAG, number + "");
        return number;
    }

}
