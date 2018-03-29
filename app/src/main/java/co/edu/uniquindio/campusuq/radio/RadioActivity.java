package co.edu.uniquindio.campusuq.radio;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;

public class RadioActivity extends MainActivity {

    public static final int MAX_VOLUME = 100;

    private ImageView play;
    private ImageView stop;
    private ImageView volume;

    public boolean volumeEnabled = false;
    private SeekBar seekBar;
    private Bundle mBundle = new Bundle();

    private MediaBrowserCompat mMediaBrowser;


    private final MediaBrowserCompat.ConnectionCallback mConnectionCallbacks = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {

            // Get the token for the MediaSession
            MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();

            // Create a MediaControllerCompat
            MediaControllerCompat mediaController = null;
            try {
                mediaController = new MediaControllerCompat(RadioActivity.this, // Context
                        token);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Save the controller
            MediaControllerCompat.setMediaController(RadioActivity.this, mediaController);

            // Finish building the UI
            buildTransportControls();
        }

        @Override
        public void onConnectionSuspended() {
            // The Service has crashed. Disable transport controls until it automatically reconnects
            Log.i(RadioActivity.class.getSimpleName(), "connection to service suspended");
        }

        @Override
        public void onConnectionFailed() {
            // The Service has refused our connection
            Log.i(RadioActivity.class.getSimpleName(), "connection to service failed");
        }
    };

    private MediaControllerCompat.Callback controllerCallback;

    public RadioActivity() {
        super.setHasSearch(false);
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_radio);
        stub.inflate();

        play = findViewById(R.id.play_button);
        stop = findViewById(R.id.stop_button);
        volume = findViewById(R.id.volume_button);
        seekBar = findViewById(R.id.seek_bar);

        controllerCallback = new MediaControllerCompat.Callback() {
            @Override
            public void onMetadataChanged(MediaMetadataCompat metadata) {}

            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                int pbState = state.getState();
                displayState(pbState);
            }
        };

        // Create MediaBrowserServiceCompat
        mMediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MediaPlaybackService.class),
                mConnectionCallbacks,
                null); // optional Bundle

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(MAX_VOLUME);
        seekBar.setProgress(MAX_VOLUME * audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) /
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (volumeEnabled) {
                    seekBar.setVisibility(View.GONE);
                    volumeEnabled = false;
                } else {
                    seekBar.setVisibility(View.VISIBLE);
                    volumeEnabled = true;
                }
            }
        });

    }

    private void buildTransportControls() {
        // Attach a listener to the button
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since this is a play/pause button, you'll need to test the current state
                // and choose the action accordingly
                int pbState = MediaControllerCompat.getMediaController(RadioActivity.this).getPlaybackState().getState();
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    play.setEnabled(false);
                    MediaControllerCompat.getMediaController(RadioActivity.this).getTransportControls().pause();
                } else if (Utilities.haveNetworkConnection(RadioActivity.this)) {
                    play.setEnabled(false);
                    MediaControllerCompat.getMediaController(RadioActivity.this).getTransportControls().play();
                } else {
                    Toast.makeText(RadioActivity.this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat.getMediaController(RadioActivity.this).getTransportControls().stop();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = (float) (1 - (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME)));
                mBundle.putFloat(getString(R.string.radio_volume), vol);
                MediaControllerCompat.getMediaController(RadioActivity.this).getTransportControls()
                        .sendCustomAction(MediaPlaybackService.CUSTOM_ACTION_SET_VOLUME, mBundle);
                if (vol == 0.0f) {
                    volume.setImageResource(R.drawable.mute_button);
                } else {
                    volume.setImageResource(R.drawable.volume_button);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(RadioActivity.this);

        // Display the initial state
        int pbState = mediaController.getPlaybackState().getState();
        displayState(pbState);

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback);

    }

    public void displayState(int state) {
        play.setEnabled(true);
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            play.setImageResource(R.drawable.pause_button);
        } else if (state == PlaybackStateCompat.STATE_CONNECTING) {
            Glide.with(RadioActivity.this).load(R.raw.spinner).into(play);
        } else  {
            play.setImageResource(R.drawable.play_button);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mMediaBrowser.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        // (see "stay in sync with the MediaSession")
        if (MediaControllerCompat.getMediaController(RadioActivity.this) != null) {
            MediaControllerCompat.getMediaController(RadioActivity.this).unregisterCallback(controllerCallback);
        }
        mMediaBrowser.disconnect();

    }


}
