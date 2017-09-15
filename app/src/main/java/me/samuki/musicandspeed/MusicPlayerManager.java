package me.samuki.musicandspeed;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;

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
    private CountDownTimer volumeTimer, durationTimer;
    private int duration, currentPosition;
    private boolean isTimerRunning;
    private long timeLeftToChangeVolume;
    private int lastMusicPlayed[];
    private int remembered;
    private int actualMusicPlaying;
    private int pausedOn;
    private ProgressBar progressBar;

    boolean isPlaying;

    int getActualMusicPlaying() {
        return actualMusicPlaying;
    }
    void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    MusicPlayerManager(Context context) {
        this.context = context;
        isTimerRunning = false;
        setLastMusicPlayed();
    }

    void playMusic() throws IOException  {
        Random random = new Random(); //AccessFile
        int playThatOne = random.nextInt(audioNames.size());
        String path = paths.get(playThatOne);

        mediaPlayer.setDataSource(path);
        mediaPlayer.prepareAsync();
        isPlaying = true;
        addToLastMusicPlayed(playThatOne);
        actualMusicPlaying = playThatOne;
        prepareProgressBar(path);
    }

    void playMusic(int whichMusic) throws IOException {
        stopMusic();
        String path = paths.get(whichMusic);
        mediaPlayer.setDataSource(path);
        mediaPlayer.prepareAsync();
        isPlaying = true;
        prepareProgressBar(path);
    }

    void nextMusic(boolean isPlaying) throws IOException {
        Random random = new Random(); //AccessFile
        int playThatOne = random.nextInt(audioNames.size());
        addToLastMusicPlayed(playThatOne);
        actualMusicPlaying = playThatOne;
        if (isPlaying) {
            durationTimer.cancel();
            playMusic(playThatOne);
        } else progressBar.setProgress(0);
    }

    void playLastMusic() throws IOException {
        actualMusicPlaying = getLastOnePlayed();
        mediaPlayer.setDataSource(paths.get(actualMusicPlaying));
        mediaPlayer.prepareAsync();
        isPlaying = true;
    }

    void restartMusic() {
        if(pausedOn == actualMusicPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            durationTimer = new CountDownTimer(duration - currentPosition, 1000) {
                @Override
                public void onTick(long l) {
                    progressBar.setProgress(duration - (int)l);
                }

                @Override
                public void onFinish() {
                    progressBar.setProgress(duration);
                }
            }.start();
        }
        else {
            try {
                playMusic(actualMusicPlaying);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            durationTimer.cancel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void pauseMusic() {
        mediaPlayer.pause();
        isPlaying = false;
        pausedOn = actualMusicPlaying;
        durationTimer.cancel();
        currentPosition = mediaPlayer.getCurrentPosition();
    }

    void stopMusic() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        isPlaying = false;
    }

    void changeVolumeDown() {
        if(isTimerRunning)
            volumeTimer.cancel();
        isTimerRunning = true;
        volumeTimer = new CountDownTimer(TIME_TO_CHANGE_VOLUME, 100) {
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
        volumeTimer.start();
    }

    void changeVolumeUp() {
        final long higherTheVolume = TIME_TO_CHANGE_VOLUME - timeLeftToChangeVolume;
        if(isTimerRunning)
            volumeTimer.cancel();
        isTimerRunning = true;
        volumeTimer = new CountDownTimer(higherTheVolume, 100) {

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
        volumeTimer.start();
    }

    private void setLastMusicPlayed() {
        lastMusicPlayed = new int[rememberThatMany];
        Arrays.fill(lastMusicPlayed, identificationNumber);
    }

    private void addToLastMusicPlayed(int number) {
        for (int i = 0; i < rememberThatMany; i++) {
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
        return number;
    }

    private void prepareProgressBar(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String durationString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = Integer.parseInt(durationString);
        progressBar.setMax(duration);
        durationTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long l) {
                progressBar.setProgress(duration - (int)l);
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(duration);
            }
        }.start();
    }
}
