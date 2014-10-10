package com.garciaericn.mediaplayer.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.garciaericn.mediaplayer.MusicPlayerService;
import com.garciaericn.mediaplayer.R;
import com.garciaericn.mediaplayer.Song;

import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * Full Sail University
 * Mobile Development BS
 * Created by ENG618-Mac on 10/5/14.
 */

public class AudioPlayerFragment extends Fragment
        implements View.OnClickListener,
        ServiceConnection, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "AudioPlayerFragment.TAG";

    private MusicPlayerService musicPlayerService;
    private boolean mBound;
    private Handler handler;

    public AudioPlayerFragment() {

    }

    public static AudioPlayerFragment newInstance() {
        return new AudioPlayerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        handler = new Handler();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView entered");

        //Load layout
        View view = inflater.inflate(R.layout.fragment_audio_player, container, false);

        // Set onClickListeners
        ImageButton playButton = (ImageButton) view.findViewById(R.id.playButton);
        playButton.setOnClickListener(this);

        ImageButton stopButton = (ImageButton) view.findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this);

        ImageButton previousButton = (ImageButton) view.findViewById(R.id.previousButton);
        previousButton.setOnClickListener(this);

        ImageButton nextButton = (ImageButton) view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);

        Switch repeatSwitch = (Switch) view.findViewById(R.id.repeatSwitch);
        repeatSwitch.setOnCheckedChangeListener(this);

        Switch shuffleSwitch = (Switch) view.findViewById(R.id.shuffleSwitch);
        shuffleSwitch.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (musicPlayerService == null) {
            // Create intent and start service
            Intent intent = new Intent(getActivity(), MusicPlayerService.class);
            activity.startService(intent);

            if (!mBound) {
                activity.bindService(intent, this, Context.BIND_AUTO_CREATE);
            }
        }
    }

    private void setInfo() {
        Song song = musicPlayerService.getCurrentSong();
        if (song != null) {
            TextView songTV = (TextView) getView().findViewById(R.id.titleTV);
            songTV.setText(song.getSongTitle());

            TextView artistTV = (TextView) getView().findViewById(R.id.authorTV);
            artistTV.setText(song.getSongAuthor());

            ImageView albumView = (ImageView) getView().findViewById(R.id.albumArt);
            albumView.setImageResource(song.getAlbumArtResourse());
        }
    }

    private void updateSeekBar() {
        final SeekBar seekBar = (SeekBar) getView().findViewById(R.id.seekBar);

        if (seekBar != null && musicPlayerService != null) {
            seekBar.setMax(musicPlayerService.getCurrentSongDuration());

            // TODO: Create thread to update every second and update seekBar.
            new Thread(new TimerTask() {
                @Override
                public void run() {
                    for (int i = 0; i < musicPlayerService.getCurrentSongDuration(); i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        seekBar.setProgress(musicPlayerService.getCurrentSongPosition());
                    }
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.shuffleSwitch : {
                if (musicPlayerService != null) {
                    musicPlayerService.setShuffle(isChecked);
                }
            }
            case R.id.repeatSwitch : {
                if (musicPlayerService != null) {
                    musicPlayerService.setRepeat(isChecked);
                }
            }
            default:break;
        }
    }

    @Override
    public void onClick(View v) {
        // Create intent to start service
        Intent intent = new Intent(getActivity(), MusicPlayerService.class);
        // Capture instance of button
        ImageButton playPause = (ImageButton) getView().findViewById(R.id.playButton);
        Switch shuffleSwitch = (Switch) getView().findViewById(R.id.shuffleSwitch);
        Switch repeatSwitch = (Switch) getView().findViewById(R.id.repeatSwitch);

        // Handle media player controls
        switch (v.getId()) {
            case R.id.playButton:
                Log.i(TAG, "Play/Pause button pressed");

                // Playing after Stop
                // Check if player does not exist
                if (musicPlayerService == null){
//                    getActivity().startService(intent);
//                    if (!mBound){
//                        getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
//                    }
                    // Change pause button to play
                    playPause.setImageResource(R.drawable.ic_action_pause);
                    // Pause from service
//                    musicPlayerService.playMedia();
                    setInfo();
                    break;
                }

                // Play from default or from paused
                //Check if player exist & is not playing
                if(musicPlayerService != null && !musicPlayerService.isPlaying) {
                    // Change play button to pause
                    playPause.setImageResource(R.drawable.ic_action_pause);
                    // Call resume from service
                    musicPlayerService.playMedia();
                    setInfo();
                    break;
                }

                // Pause from playing
                // Check if player exist & is playing
                if (musicPlayerService != null && musicPlayerService.isPlaying) {
                    // Change pause button to play
                    playPause.setImageResource(R.drawable.ic_action_play);
                    // Pause from service
                    musicPlayerService.pauseMedia();
//                    setInfo();
                    break;
                }


                break;

            case R.id.stopButton:
                Log.i(TAG, "Stop button pressed");
                if (musicPlayerService != null) {
                    // Call stopMedia from service
                    musicPlayerService.stopMedia();
                    // Stop service
                getActivity().stopService(intent);
                    // Set media player to null
//                    musicPlayerService = null;
                    // Change pause button to play
                    playPause.setImageResource(R.drawable.ic_action_play);
                }
                break;

            case R.id.previousButton:


                Log.i(TAG, "Previous button pressed");
                musicPlayerService.previousSong();
                setInfo();
                break;

            case R.id.nextButton:
                Log.i(TAG, "Next button pressed");
                musicPlayerService.nextSong();
                setInfo();
                break;

            default:
                break;
        }
    }

    /**
     * Service Binder Methods
     */

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicPlayerService.MusicPlayerBinder binder = (MusicPlayerService.MusicPlayerBinder) service;
        musicPlayerService = binder.getService();
        musicPlayerService.playMedia();
        setInfo();
        mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicPlayerService = null;
        mBound = false;
    }
}