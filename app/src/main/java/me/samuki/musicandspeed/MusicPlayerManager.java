//package me.samuki.musicandspeed;
//
//import android.media.MediaMetadataRetriever;
//import android.media.MediaPlayer;
//import android.os.CountDownTimer;
//import android.widget.ProgressBar;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Random;
//
//import static me.samuki.musicandspeed.MusicService.fastDrivingSongs;
//import static me.samuki.musicandspeed.MusicService.audioPaths;
//import static me.samuki.musicandspeed.MusicService.slowDrivingSongs;
//
//class MusicPlayerManager {
//    private static final long TIME_TO_CHANGE_VOLUME = 5000;
//    private static final int rememberThatMany = 50;
//    private static final int identificationNumber = -1;
//
//    MediaPlayer mediaPlayer;
//
//    private CountDownTimer volumeTimer, durationTimer;
//    private int duration, currentPosition;
//    private boolean isTimerRunning, isDurationTimerRunning;
//    private long timeLeftToChangeVolume;
//    private int lastMusicPlayed[];
//    private int remembered;
//    private int actualMusicPlaying;
//    private int pausedOn;
//    private ProgressBar progressBar;
//
//    boolean isPlaying;
//    boolean firstServicePlay;
//    boolean fastDrivingModeActive;
//
//    int getActualMusicPlaying() {
//        return actualMusicPlaying;
//    }
//    void setProgressBar(final ProgressBar progressBar) {
//        this.progressBar = progressBar;
//        if(!firstServicePlay)
//            currentPosition = mediaPlayer.getCurrentPosition();
//        else {
//            duration = 0;
//            currentPosition = 0;
//        }
//        if (isDurationTimerRunning && isPlaying) {
//            durationTimer.cancel();
//            progressBar.setMax(duration);
//            durationTimer = new CountDownTimer(duration - currentPosition, 1000) {
//                @Override
//                public void onTick(long l) {
//                    progressBar.setProgress(duration - (int) l);
//                }
//
//                @Override
//                public void onFinish() {
//                    progressBar.setProgress(duration);
//                    isDurationTimerRunning = false;
//                }
//            }.start();
//        } else if(!isPlaying) {
//            if(isDurationTimerRunning)
//                durationTimer.cancel();
//            progressBar.setMax(duration);
//            progressBar.setProgress(currentPosition);
//        }
//    }
//
//    MusicPlayerManager() {
//        isTimerRunning = false;
//        setLastMusicPlayed();
//        firstServicePlay = true;
//    }
//
//    void playMusic() throws IOException  {
//        Random random = new Random(); //AccessFile
//        int listIndex = random.nextInt(slowDrivingSongs.size());
//        int playThatOne = slowDrivingSongs.get(listIndex);
//        String path = audioPaths.get(playThatOne);
//
//        mediaPlayer.setDataSource(path);
//        mediaPlayer.prepareAsync();
//        isPlaying = true;
//        addToLastMusicPlayed(playThatOne);
//        actualMusicPlaying = playThatOne;
//        prepareProgressBar(path);
//    }
//
//    void playMusic(int whichMusic, boolean previous) throws IOException {
//        stopMusic();
//        String path = audioPaths.get(whichMusic);
//        mediaPlayer.setDataSource(path);
//        mediaPlayer.prepareAsync();
//        isPlaying = true;
//        if(!previous)
//            addToLastMusicPlayed(whichMusic);
//        prepareProgressBar(path);
//        actualMusicPlaying = whichMusic;
//    }
//
//    private void playFastDrivingMusic() throws IOException {
//        stopMusic();
//        Random random = new Random(); //AccessFile
//        int listIndex = random.nextInt(fastDrivingSongs.size());
//        int playThatOne = fastDrivingSongs.get(listIndex);
//        String path = audioPaths.get(playThatOne);
//
//        mediaPlayer.setDataSource(path);
//        mediaPlayer.prepareAsync();
//        isPlaying = true;
//        fastDrivingModeActive = true;
//        addToLastMusicPlayed(playThatOne);
//        actualMusicPlaying = playThatOne;
//        prepareProgressBar(path);
//    }
//
//    void nextMusic(boolean isPlaying) throws IOException {
//        stopMusic();
//
//        Random random = new Random(); //AccessFile
//        int listIndex;
//        int playThatOne;
//        if(!fastDrivingModeActive) {
//            listIndex = random.nextInt(slowDrivingSongs.size());
//            playThatOne = slowDrivingSongs.get(listIndex);
//        } else {
//            listIndex = random.nextInt(fastDrivingSongs.size());
//            playThatOne = fastDrivingSongs.get(listIndex);
//        }
//        addToLastMusicPlayed(playThatOne);
//        actualMusicPlaying = playThatOne;
//        if (isPlaying) {
//            durationTimer.cancel();
//            playMusic(playThatOne, false);
//        } else progressBar.setProgress(0);
//    }
//
//    private void playLastMusic() throws IOException {
//        actualMusicPlaying = getLastOnePlayed();
//        String path = audioPaths.get(actualMusicPlaying);
//        mediaPlayer.setDataSource(path);
//        mediaPlayer.prepareAsync();
//        isPlaying = true;
//        prepareProgressBar(path);
//    }
//
//    void restartMusic() {
//        if(pausedOn == actualMusicPlaying) {
//            mediaPlayer.start();
//            isPlaying = true;
//            if (isDurationTimerRunning)
//                durationTimer.cancel();
//            durationTimer = new CountDownTimer(duration - currentPosition, 1000) {
//                @Override
//                public void onTick(long l) {
//                    progressBar.setProgress(duration - (int)l);
//                }
//
//                @Override
//                public void onFinish() {
//                    progressBar.setProgress(duration);
//                    isDurationTimerRunning = false;
//                }
//            }.start();
//            isDurationTimerRunning = true;
//        }
//        else {
//            try {
//                playMusic(actualMusicPlaying, false);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    void previousMusic() {
//        try {
//            if (isPlaying)
//                durationTimer.cancel();
//            if(remembered != 0) {
//                boolean tmpIsPlaying = isPlaying;
//                stopMusic();//This action gonna change the value of isPlaying!
//                if (tmpIsPlaying)
//                    playLastMusic();
//                else
//                    actualMusicPlaying = getLastOnePlayed();
//            } else if(isPlaying) playMusic(actualMusicPlaying, true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    void pauseMusic() {
//        mediaPlayer.pause();
//        isPlaying = false;
//        pausedOn = actualMusicPlaying;
//        durationTimer.cancel();
//        currentPosition = mediaPlayer.getCurrentPosition();
//    }
//
//    void stopMusic() {
//        mediaPlayer.stop();
//        mediaPlayer.reset();
//        isPlaying = false;
//    }
//
//    void changeVolumeDown() {
//        if(isTimerRunning)
//            volumeTimer.cancel();
//        isTimerRunning = true;
//        volumeTimer = new CountDownTimer(TIME_TO_CHANGE_VOLUME, 100) {
//            @Override
//            public void onTick(long l) {
//                float lowerVolume = (float)l/(float)TIME_TO_CHANGE_VOLUME;
//                mediaPlayer.setVolume(lowerVolume, lowerVolume);
//                timeLeftToChangeVolume = l;
//            }
//
//            @Override
//            public void onFinish() {
//                try {
//                    stopMusic();
//                    playFastDrivingMusic();
//                    mediaPlayer.setVolume(1,1);
//                } catch (IOException ignored) {
//                }
//            }
//        };
//        volumeTimer.start();
//    }
//
//    void changeVolumeUp() {
//        final long higherTheVolume = TIME_TO_CHANGE_VOLUME - timeLeftToChangeVolume;
//        if(isTimerRunning)
//            volumeTimer.cancel();
//        isTimerRunning = true;
//        volumeTimer = new CountDownTimer(higherTheVolume, 100) {
//
//            @Override
//            public void onTick(long l) {
//                float lowerVolume = ((float)TIME_TO_CHANGE_VOLUME - (float)l)/(float)TIME_TO_CHANGE_VOLUME;
//                mediaPlayer.setVolume(lowerVolume, lowerVolume);
//            }
//
//            @Override
//            public void onFinish() {
//                mediaPlayer.setVolume(1, 1);
//            }
//        };
//        volumeTimer.start();
//    }
//
//    private void setLastMusicPlayed() {
//        lastMusicPlayed = new int[rememberThatMany];
//        Arrays.fill(lastMusicPlayed, identificationNumber);
//    }
//
//    private void addToLastMusicPlayed(int number) {
//        for (int i = 0; i < rememberThatMany; i++) {
//            if(lastMusicPlayed[i] == identificationNumber) {
//                lastMusicPlayed[i] = number;
//                remembered = i;
//                break;
//            }
//            else {
//                int tmp = number;
//                number = lastMusicPlayed[i];
//                lastMusicPlayed[i] = tmp;
//            }
//        }
//    }
//
//    private int getLastOnePlayed() {
//        int number = lastMusicPlayed[remembered];
//        if(remembered > 0) {
//            lastMusicPlayed[remembered] = identificationNumber;
//            for (int i = remembered - 1; i > 0; i--) {
//                int tmp = number;
//                number = lastMusicPlayed[i];
//                lastMusicPlayed[i] = tmp;
//            }
//            remembered--;
//        }
//        if(actualMusicPlaying == number)
//            return getLastOnePlayed();
//        return number;
//    }
//
//    private void prepareProgressBar(String path) {
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        mmr.setDataSource(path);
//        String durationString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//        duration = Integer.parseInt(durationString);
//        progressBar.setMax(duration);
//        if (isDurationTimerRunning)
//            durationTimer.cancel();
//        durationTimer = new CountDownTimer(duration, 1000) {
//            @Override
//            public void onTick(long l) {
//                progressBar.setProgress(duration - (int)l);
//            }
//
//            @Override
//            public void onFinish() {
//                progressBar.setProgress(duration);
//                isDurationTimerRunning = false;
//            }
//        }.start();
//        isDurationTimerRunning = true;
//    }
//}
