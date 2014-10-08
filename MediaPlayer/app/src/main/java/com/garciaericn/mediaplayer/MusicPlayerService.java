package com.garciaericn.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Full Sail University
 * Mobile Development BS
 * Created by ENG618-Mac on 10/5/14.
 */
public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener {

    public static boolean isPlaying;

    private MediaPlayer mediaPlayer;
    private int currentSong;
    private ArrayList<Song> songsArray;
    private boolean mPrepared;
    private boolean mActivityResumed;
    private int mAudioPosition;


    public class MusicPlayerBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    MusicPlayerBinder musicPlayerBinder;

    private static final int FOREGROUD_NOTIFICATION = 0x323d55;

    @Override
    public void onCreate() {
        super.onCreate();

        currentSong = 0;

        songsArray = new ArrayList<Song>();

//        songsArray.add(new Song("android.resource://" + getPackageName() + "/" + R.raw.blown_away, "Blown Away", "Kevin MacLeod"));
//        songsArray.add(new Song("android.resource://" + getPackageName() + "/" + R.raw.carefree, "Carefree", "Kevin MacLeod"));
//        songsArray.add(new Song("android.resource://" + getPackageName() + "/" + R.raw.master_of_the_feast, "Master of the Feast", "Kevin MacLeod"));


//        musicPlayerBinder = new MusicPlayerBinder();
        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_action_play)
                .setContentTitle("Service Running")
                .setContentText("Track info here.")
                .setAutoCancel(false)
                .setOngoing(true);

        startForeground(FOREGROUD_NOTIFICATION, builder.build());

        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return Service.START_NOT_STICKY;
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

    public void showToastFromService() {
        Toast.makeText(this, "Toast from service", Toast.LENGTH_SHORT).show();
    }

    /**
     * Media Player onPreparedListener
     * */

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPrepared = true;

        if(mediaPlayer != null && mActivityResumed) {
            mediaPlayer.seekTo(mAudioPosition);
            mediaPlayer.start();
        }
    }
}
