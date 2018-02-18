package co.edu.uniquindio.campusuq.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import co.edu.uniquindio.campusuq.R;

public class RadioActivity extends MainActivity {

    public final static int mNotificationId = 1;
    public final static String stream = "http://72.29.81.205:9030/;";
    private final static int MAX_VOLUME = 100;

    private ImageView play;
    private ImageView stop;
    private ImageView volume;

    public boolean volumeEnabled = false;
    private SeekBar seekBar;
    public AudioManager audioManager;

    public MediaPlayer mediaPlayer;
    public boolean started = false;
    public boolean prepared = false;
    private ProgressDialog pDialog;

    public RadioActivity() {
        super.setHasSearch(false);
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent() {
        super.addContent();

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_radio);
        View inflated = stub.inflate();

        pDialog = new ProgressDialog(this);
        pDialog.setTitle(getString(R.string.loading_content));
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        //pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //pDialog.setMax(100);

        play = (ImageView) findViewById(R.id.play_button);
        stop = (ImageView) findViewById(R.id.stop_button);
        volume = (ImageView) findViewById(R.id.volume_button);

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(MAX_VOLUME);
        seekBar.setProgress(MAX_VOLUME*audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)/audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = (float) (1 - (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME)));
                mediaPlayer.setVolume(vol, vol);
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

        stop.setEnabled(false);
        initializeMediaPlayer();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prepared) {
                    if (started) {
                        mediaPlayer.pause();
                        started = false;
                        play.setImageResource(R.drawable.play_button);
                    } else {
                        mediaPlayer.start();
                        started = true;
                        play.setImageResource(R.drawable.pause_button);
                    }
                } else if (haveNetworkConnection(RadioActivity.this)) {
                    startPlaying();
                } else {
                    Toast.makeText(RadioActivity.this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prepared) {
                    stopPlaying();
                }
            }
        });

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

    public void initializeMediaPlayer() {
        //pDialog.setProgress(0);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.getAudioSessionId();
        try {
            mediaPlayer.setDataSource(stream);
            /*
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    pDialog.setProgress(percent);
                }
            });
            */
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                    startPlaying();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPlaying() {
        pDialog.show();
        try {
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    stop.setEnabled(true);
                    mediaPlayer.start();
                    prepared = true;
                    started = true;
                    play.setImageResource(R.drawable.pause_button);
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlaying() {
        if (started) {
            stop.setEnabled(false);
            mediaPlayer.stop();
            mediaPlayer.release();
            prepared = false;
            started = false;
            play.setImageResource(R.drawable.play_button);
            initializeMediaPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (started) {
            createNotification();
        } else if (prepared) {
            mediaPlayer.release();
        }
    }

    public void createNotification() {

        NotificationCompat.Builder mBuilder;

        /*
        ComponentName myEventReceiver = new ComponentName(getPackageName(), RemoteControlEventReceiver.class.getName());
        AudioManager myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myAudioManager.registerMediaButtonEventReceiver(myEventReceiver);
        // build the PendingIntent for the remote control client
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(myEventReceiver);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);
        // create and register the remote control client
        RemoteControlClient myRemoteControlClient = new RemoteControlClient(mediaPendingIntent);
        myAudioManager.registerRemoteControlClient(myRemoteControlClient);
        */

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // The id of the channel.
            String id = getString(R.string.radio_notification_id);
            // The user-visible name of the channel.
            CharSequence name = getString(R.string.radio_notification_name);
            // The user-visible description of the channel.
            String description = getString(R.string.radio_notification_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            //mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            //mChannel.setLightColor(Color.GREEN);
            //mChannel.enableVibration(true);
            //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);

            mBuilder = new NotificationCompat.Builder(this, id)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(""+R.string.radio_notification_name)
                    .setContentText(""+R.string.radio_notification_description);
        } else {
            mBuilder = new NotificationCompat.Builder(this)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(""+R.string.radio_notification_name)
                    .setContentText(""+R.string.radio_notification_description)
                    //.addAction(R.drawable.play_button, "Play", playPendingIntent) // #0
                    //.addAction(R.drawable.pause_button, "Pause", playPendingIntent)  // #1
                    //.addAction(R.drawable.stop_button, "Stop", playPendingIntent)     // #2
                    // Apply the media style template
                    //.setStyle(new android.support.v4.media.app.NotificationCompat.
                    //        .setShowActionsInCompactView(new int[]{0, 2})
                    //        .setMediaSession(mMediaSession.getSessionToken()));
            ;
        }

        //NotificationCompat.DecoratedCustomViewStyle customStyle = new NotificationCompat.DecoratedCustomViewStyle();
        //mBuilder.setStyle(customStyle);

        mBuilder.setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, RadioActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(RadioActivity.class);
        stackBuilder.editIntentAt(0).putExtra("CATEGORY", getString(R.string.app_title_menu));
        stackBuilder.editIntentAt(1).putExtra("CATEGORY", getString(R.string.services_module));
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.notify(mNotificationId, mBuilder.build());

    }

    public class RemoteControlEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

}
