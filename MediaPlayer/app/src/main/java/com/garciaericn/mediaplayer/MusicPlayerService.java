package com.garciaericn.mediaplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.garciaericn.mediaplayer.activities.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Full Sail University
 * Mobile Development BS
 * Created by ENG618-Mac on 10/5/14.
 */
public class MusicPlayerService extends Service
        implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private static final String TAG = "MusicPlayerService.TAG";
    private static final int FOREGROUND_NOTIFICATION = 0x323d55;
    private static final int REQUEST_NOTIFY_LAUNCH = 0X543d34;

    private MusicPlayerBinder musicPlayerBinder;
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private boolean mPrepared;
    private ArrayList<Song> songsArray;
    private int currentSongPosition;
    public boolean isPlaying;
    private int mAudioPosition;
    private boolean shuffle;
    private boolean repeat;

    public class MusicPlayerBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Set current song to 1st position in array
        currentSongPosition = 0;
        // Set default position to 0
        mAudioPosition = 0;
        // Set is playing & prepared to false default
        isPlaying = mPrepared = shuffle = repeat = false;

        // Verify if songs array is already populated
        if (songsArray == null) {
            // Initialize array
            songsArray = new ArrayList<Song>();

            // Add songs to array via Song object
            songsArray.add(new Song("android.resource://" + getPackageName() + "/" + R.raw.blown_away, "Blown Away", "Kevin MacLeod", R.drawable.albumart1, BitmapFactory.decodeResource(getResources(), R.drawable.albumart1)));
            songsArray.add(new Song("android.resource://" + getPackageName() + "/" + R.raw.carefree, "Carefree", "Kevin MacLeod", R.drawable.albumart2, BitmapFactory.decodeResource(getResources(), R.drawable.albumart2)));
            songsArray.add(new Song("android.resource://" + getPackageName() + "/" + R.raw.master_of_the_feast, "Master of the Feast", "Kevin MacLeod", R.drawable.albumart3, BitmapFactory.decodeResource(getResources(), R.drawable.albumart3)));
        }

        // Create and cache binder
        musicPlayerBinder = new MusicPlayerBinder();

        // Show toast the service was created
        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer == null) {
            // Initialize media player, set audio type, set onPreparedListener
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            isPlaying = false;
            mPrepared = false;
        }

        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "Service Bound", Toast.LENGTH_SHORT).show();
        return musicPlayerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "Service Unbound", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    private void setNotification(String title, String content) {

        // API 16 required for this style pending intent, with TaskStackBuilder
//        // Create intent for activity
//        Intent intent = new Intent(this, MainActivity.class);
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(MainActivity.class);
//        stackBuilder.addNextIntent(intent);
//
//        PendingIntent mainPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_NOTIFY_LAUNCH, intent, 0);

        // Build Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_action_play)
                .setLargeIcon(currentSong.getAlbumArt())
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setOngoing(true);

        builder.setContentIntent(pendingIntent);

        // API 16 required for this call
//        builder.setContentIntent(mainPendingIntent);

        startForeground(FOREGROUND_NOTIFICATION, builder.build());
    }

    private void dismissNotification() {
        stopForeground(true);
        NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.cancel(FOREGROUND_NOTIFICATION);
    }

    /**
     * Media Player onPreparedListener
     * */

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPrepared = true;

        if(mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    /**
     * Player Methods Helper Methods
     * */

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
        if (mediaPlayer != null) {
            mediaPlayer.isLooping();
        }
    }

    // Verify is media is currently playing
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextSong();
    }

    // Verify if player is prepared
    public boolean isPrepared(){
        return mPrepared;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public int getCurrentSongDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentSongPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * Player Methods
     * */

     // Start media player
    public void playMedia(){
        // Prepare MP
        if (mediaPlayer != null && songsArray != null && !mPrepared) {

            try {
                // Get instance of current song
                currentSong = songsArray.get(currentSongPosition);
                // Set data source
                mediaPlayer.setDataSource(this, Uri.parse(currentSong.getSongString()));
                // Set notification
                setNotification(currentSong.getSongTitle(), "Artist: " + currentSong.getSongAuthor());
                // Set is playing to true
                isPlaying = true;
            } catch(IOException e) {
                e.printStackTrace();
                Log.i(TAG, "Media player error: " + e);

                mediaPlayer.release();
                mediaPlayer = null;
                isPlaying = mPrepared = false;
            }
            mediaPlayer.prepareAsync();
        } else { // Otherwise resume media
            resumeMedia();
        }
    }

    // Pause media
    public void pauseMedia() {
        // Pause media
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mAudioPosition = mediaPlayer.getCurrentPosition();
            mPrepared = true;
            isPlaying = false;
        }
    }

    // Resume playing
    public void resumeMedia() {
        if(mediaPlayer != null && !mPrepared) {
            mediaPlayer.prepareAsync();
        } else if(mediaPlayer != null && mPrepared) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    //Next song
    public void nextSong() {
        if (!shuffle && !repeat) {
            if ((songsArray.size()-1) > currentSongPosition) {
                // Increment to next song
                currentSongPosition++;
                // Get instance of current song
                currentSong = songsArray.get(currentSongPosition);
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(this, Uri.parse(currentSong.getSongString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.prepareAsync();
                // Set notification
                setNotification(currentSong.getSongTitle(), "Artist: " + currentSong.getSongAuthor());
                // Set is playing to true
                isPlaying = true;

            } else {
                Toast.makeText(this, "On last song in list", Toast.LENGTH_SHORT).show();
            }
        }

        if (!repeat && shuffle) {
            playRandomSong();
        }

        if (repeat) {
            // Get instance of current song
            repeatCurrentSong();
        }
    }

    // Previous song
    public void previousSong() {
        if (!shuffle && !repeat) {
            if (currentSongPosition > 0) {
                // Increment to next song
                currentSongPosition--;
                // Get instance of current song
                currentSong = songsArray.get(currentSongPosition);
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(this, Uri.parse(currentSong.getSongString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.prepareAsync();
                // Set notification
                setNotification(currentSong.getSongTitle(), "Artist: " + currentSong.getSongAuthor());
                // Set is playing to true
                isPlaying = true;

            } else {
                Toast.makeText(this, "On first song in list", Toast.LENGTH_SHORT).show();
            }
        } else if (!repeat && shuffle) { // Shuffle song
            playRandomSong();
        } else if (repeat) { // Repeat current song
            repeatCurrentSong();
        }
    }

    private void repeatCurrentSong() {
        // Get instance of current song
        mediaPlayer.stop();
        currentSong = songsArray.get(currentSongPosition);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(this, Uri.parse(currentSong.getSongString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        // Set notification
        setNotification(currentSong.getSongTitle(), "Artist: " + currentSong.getSongAuthor());
        // Set is playing to true
        isPlaying = true;
    }

    private void playRandomSong() {
        int min = 0;
        int max = songsArray.size() - 1;

        Random random = new Random();
        currentSongPosition = random.nextInt((max - min)+1);

        mediaPlayer.stop();
        currentSong = songsArray.get(currentSongPosition);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(this, Uri.parse(currentSong.getSongString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        // Set notification
        setNotification(currentSong.getSongTitle(), "Artist: " + currentSong.getSongAuthor());
        // Set is playing to true
        isPlaying = true;
    }

    // Stop Media
    public void stopMedia() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
            dismissNotification();
            releaseMediaPlayer();
        }
    }

    // Release media player
    public void releaseMediaPlayer() {
        if(mediaPlayer != null) {
            mPrepared = false;
            mediaPlayer.reset();
        }
    }

    /**
     * OnErrorListener
     * */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.i(TAG, "There was an error in MediaPlayer: " + mp + " What: " + what + " Extra: " + extra);
        Toast.makeText(this, "Error: Please try again", Toast.LENGTH_LONG).show();
        mp.release();
        mPrepared = isPlaying = false;
        return false;
    }
}