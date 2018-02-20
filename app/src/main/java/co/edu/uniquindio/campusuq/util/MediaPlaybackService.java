package co.edu.uniquindio.campusuq.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.RadioActivity;

/**
 * Created by Juan Camilo on 18/02/2018.
 */

public class MediaPlaybackService extends MediaBrowserServiceCompat {

    public final static String stream = "http://72.29.81.205:9030/;";
    public final static String CUSTOM_ACTION_SET_VOLUME = "SET_VOLUME";

    private boolean prepared = false;
    private boolean started = false;
    private boolean hasMedia = true;

    private static final String LOG_TAG = MediaPlaybackService.class.getSimpleName();
    private static final String MEDIA_ROOT_ID = "media_root_id";
    private static final String EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private MediaPlayer mMediaPlayer = null;
    private Bundle mBundle = new Bundle();
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;

    private final static int mNotificationId = 1;
    private Notification playNotification;
    private Notification pauseNotification;
    private Notification connectNotification;

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        // Create a MediaSessionCompat
        mMediaSession = new MediaSessionCompat(getApplicationContext(), LOG_TAG);

        // Enable callbacks from MediaButtons and TransportControls
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mBundle.putFloat(getString(R.string.radio_volume), RadioActivity.MAX_VOLUME);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_STOP)
                .addCustomAction(new PlaybackStateCompat.CustomAction.Builder(
                        CUSTOM_ACTION_SET_VOLUME, getString(R.string.radio_volume), R.drawable.volume_button)
                        .setExtras(mBundle)
                        .build());
        mMediaSession.setPlaybackState(mStateBuilder.build());

        // Create callback
        afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {

            }
        };
        MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                if (!started) {
                    // Request audio focus for playback, this registers the afChangeListener
                    int result = audioManager.requestAudioFocus(afChangeListener,
                            // Use the music stream.
                            AudioManager.STREAM_MUSIC,
                            // Request permanent focus.
                            AudioManager.AUDIOFOCUS_GAIN);
                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        // Start the service
                        Intent intent = new Intent(getApplicationContext(), MediaPlaybackService.class);
                        startService(intent);
                        // Set the session active
                        mMediaSession.setActive(true);
                        // start the player (custom call)
                        if (!prepared) {
                            // update state
                            PlaybackStateCompat playbackState = mMediaSession.getController().getPlaybackState();
                            playbackState = mStateBuilder
                                    .setState(PlaybackStateCompat.STATE_CONNECTING,
                                            playbackState.getPosition(),
                                            playbackState.getPlaybackSpeed())
                                    .build();
                            mMediaSession.setPlaybackState(playbackState);
                            try {
                                mMediaPlayer.prepareAsync();
                            } catch (Exception e) {
                                Toast.makeText(MediaPlaybackService.this,
                                        getString(R.string.loading_content), Toast.LENGTH_SHORT).show();
                            }
                            // Put the service in the foreground, post notification
                            startForeground(mNotificationId, connectNotification);
                        } else {
                            mMediaPlayer.start();
                            started = true;
                            // update state
                            PlaybackStateCompat playbackState = mMediaSession.getController().getPlaybackState();
                            playbackState = mStateBuilder
                                    .setState(PlaybackStateCompat.STATE_PLAYING,
                                            playbackState.getPosition(),
                                            playbackState.getPlaybackSpeed())
                                    .build();
                            mMediaSession.setPlaybackState(playbackState);
                            // Put the service in the foreground, post notification
                            startForeground(mNotificationId, pauseNotification);
                        }
                    }
                }
            }

            @Override
            public void onPause() {
                if (started) {
                    // pause the player (custom call)
                    mMediaPlayer.pause();
                    started = false;
                    // Update state
                    PlaybackStateCompat playbackState = mMediaSession.getController().getPlaybackState();
                    playbackState = mStateBuilder
                            .setState(PlaybackStateCompat.STATE_PAUSED,
                                    playbackState.getPosition(),
                                    playbackState.getPlaybackSpeed())
                            .build();
                    mMediaSession.setPlaybackState(playbackState);
                    // Take the service out of the foreground, retain the notification
                    startForeground(mNotificationId, playNotification);
                    stopForeground(false);
                }
            }

            @Override
            public void onStop() {
                // Abandon audio focus
                audioManager.abandonAudioFocus(afChangeListener);
                // update state
                PlaybackStateCompat playbackState = mMediaSession.getController().getPlaybackState();
                playbackState = mStateBuilder
                        .setState(PlaybackStateCompat.STATE_STOPPED,
                                playbackState.getPosition(),
                                playbackState.getPlaybackSpeed())
                        .build();
                mMediaSession.setPlaybackState(playbackState);
                // Set the session inactive
                mMediaSession.setActive(false);
                // stop the player (custom call)
                if (started || prepared) {
                    mMediaPlayer.stop();
                    prepared = false;
                    started = false;
                }
                // Stop the service
                stopSelf();
                // Take the service out of the foreground
                stopForeground(true);
            }

            @Override
            public void onCustomAction(String action, Bundle extras) {
                if (CUSTOM_ACTION_SET_VOLUME.equals(action)) {
                    float vol = extras.getFloat(getString(R.string.radio_volume));
                    mMediaPlayer.setVolume(vol, vol);
                }
            }
        };

        // MySessionCallback() has methods that handle callbacks from a media controller
        mMediaSession.setCallback(callback);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(getApplicationContext(), MediaButtonReceiver.class);
        PendingIntent mbrIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);
        mMediaSession.setMediaButtonReceiver(mbrIntent);

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mMediaSession.getSessionToken());

        mMediaSession.setActive(true);

        initMediaPlayer();

        playNotification = createNotification("Play");
        pauseNotification = createNotification("Pause");
        connectNotification = createNotification("Connect");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            // Try to handle the intent as a media button event wrapped by MediaButtonReceiver
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
        return START_STICKY;
    }

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            return new BrowserRoot(MEDIA_ROOT_ID, null);
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierachy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            return new BrowserRoot(EMPTY_MEDIA_ROOT_ID, null);
        }
    }

    public boolean allowBrowsing(String clientPackageName, int clientUid) {
        if (clientPackageName.equals(getApplication().getPackageName())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onLoadChildren(final String parentMediaId, final Result<List<MediaBrowserCompat.MediaItem>> result) {
        //  Browsing not allowed
        if (TextUtils.equals(EMPTY_MEDIA_ROOT_ID, parentMediaId)) {
            result.sendResult(null);
            return;
        }

        // Assume for example that the music catalog is already loaded/cached.

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        // Check if this is the root menu:
        if (MEDIA_ROOT_ID.equals(parentMediaId)) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems);
    }

    public void initMediaPlayer() {
        // ...initialize the MediaPlayer here...
        mMediaPlayer = new MediaPlayer();
        // ... other initialization here ...
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(stream);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    hasMedia = true;
                    prepared = true;
                    mp.start();
                    started = true;
                    PlaybackStateCompat playbackState = mMediaSession.getController().getPlaybackState();
                    playbackState = mStateBuilder
                            .setState(PlaybackStateCompat.STATE_PLAYING,
                                    playbackState.getPosition(),
                                    playbackState.getPlaybackSpeed())
                            .build();
                    mMediaSession.setPlaybackState(playbackState);
                    // Put the service in the foreground, post notification
                    startForeground(mNotificationId, pauseNotification);
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (hasMedia) {
                        try {
                            mp.stop();
                            mp.prepareAsync();
                        } catch (Exception e) {
                            hasMedia = false;
                            AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                            am.abandonAudioFocus(afChangeListener);
                            PlaybackStateCompat playbackState = mMediaSession.getController().getPlaybackState();
                            playbackState = mStateBuilder
                                    .setState(PlaybackStateCompat.STATE_STOPPED,
                                            playbackState.getPosition(),
                                            playbackState.getPlaybackSpeed())
                                    .build();
                            mMediaSession.setPlaybackState(playbackState);
                            mMediaSession.setActive(false);
                            if (started || prepared) {
                                mMediaPlayer.stop();
                                prepared = false;
                                started = false;
                            }
                            stopSelf();
                            stopForeground(true);
                            Toast.makeText(MediaPlaybackService.this,
                                    getString(R.string.radio_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) mMediaPlayer.release();
    }

    public Notification createNotification(String type) {
        // Given a media session and its context (usually the component containing the session)
        // Create a NotificationCompat.Builder

        NotificationCompat.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // The id of the channel.
            String nId = getString(R.string.radio_notification_id);
            // The user-visible name of the channel.
            CharSequence nName = getString(R.string.radio_notification_name);
            // The user-visible description of the channel.
            String nDescription = getString(R.string.radio_notification_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(nId, nName, importance);
            // Configure the notification channel.
            mChannel.setDescription(nDescription);
            mNotificationManager.createNotificationChannel(mChannel);

            builder = new NotificationCompat.Builder(getApplicationContext(), nId);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        Intent resultIntent = new Intent(getApplicationContext(), RadioActivity.class);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(RadioActivity.class);
        stackBuilder.editIntentAt(0).putExtra("CATEGORY", getString(R.string.app_title_menu));
        stackBuilder.editIntentAt(1).putExtra("CATEGORY", getString(R.string.services_module));
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder
                // Add the metadata for the currently playing track
                .setContentTitle(getString(R.string.radio_notification_id))
                .setContentText(getString(R.string.radio_notification_name))
                .setSubText(getString(R.string.radio_notification_description))
                .setLargeIcon(BitmapFactory.decodeResource(
                        getApplicationContext().getResources(), R.drawable.app_icon))

                // Enable launching the player by clicking the notification
                .setContentIntent(resultPendingIntent)

                // Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
                        PlaybackStateCompat.ACTION_STOP))

                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Add an app icon and set its accent color
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setUsesChronometer(true);

        if (type.equals("Play")) {
            builder
                    // Add a play button
                    .addAction(new NotificationCompat.Action(
                            R.drawable.ic_play_notification, "Play",
                            MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
                                    PlaybackStateCompat.ACTION_PLAY)));
        } else if (type.equals("Pause")) {
            builder
                    // Add a pause button
                    .addAction(new NotificationCompat.Action(
                            R.drawable.ic_pause_notification, "Pause",
                            MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
                                    PlaybackStateCompat.ACTION_PAUSE)));
        } else {
            builder
                    // Add a pause button
                    .addAction(new NotificationCompat.Action(
                            R.drawable.ic_loading_notification, "Loading",
                            resultPendingIntent));
        }

        builder
                // Add a stop button
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_stop_notification, "Stop",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
                                PlaybackStateCompat.ACTION_STOP)))

                // Take advantage of MediaStyle features
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));

        // Display the notification and place the service in the foreground
        return builder.build();
    }


}